package com.example.chatfoy.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatfoy.R;
import com.example.chatfoy.model.object.Chat;
import com.example.chatfoy.model.object.RoomChat;
import com.example.chatfoy.model.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListRoomAdapter extends RecyclerView.Adapter <ListRoomAdapter.ViewHolderRoom>{
    private static final String TAG = "AAA" ;
    private Context context;

    private OnItemClickListener mlistener;

    public void setOnItemClickListener(OnItemClickListener mlistener) {
        this.mlistener = mlistener;
    }

    private ArrayList<RoomChat> listRoom;
    String myId= FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refChat = db.collection("listRoom");
    CollectionReference refUser = db.collection("users");

    public ListRoomAdapter(Context context, ArrayList<RoomChat> listRoom) {
        this.context = context;
        this.listRoom = listRoom;
    }

    @NonNull
    @Override
    public ViewHolderRoom onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_room,parent,false);
        return new ViewHolderRoom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderRoom holder, int position) {
        RoomChat roomChat = listRoom.get(position);
        ArrayList<String> listIdMember = roomChat.getListIdMember();
        for (int i=0;i<listIdMember.size();i++){
            String userId = listIdMember.get(i);
            if (!userId.equals(myId)){
               refUser.document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if (!task.isSuccessful()) {
                           Log.d(TAG, "fail to load avatar and roomname");
                           return;
                       }
                       User user = task.getResult().toObject(User.class);
                       holder.txtRoomName.setText(user.getFullName());
                       if (user.getUrlAvatar().equals("default")){
                           holder.avatar.setImageResource(R.mipmap.ic_logo);
                       }else {
                           Glide.with(context).load(user.getUrlAvatar()).into(holder.avatar);
                       }
                       if (user.isOnline()){
                           holder.status.setVisibility(View.VISIBLE);
                       }else {
                           holder.status.setVisibility(View.GONE);
                       }
                   }
               });
            }
        }
        refChat.document(roomChat.getRoomId()).collection("chats")
                .orderBy("time", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e!=null) {
                            Log.d(TAG, "appear an erros with load last chat");
                            return;
                        }
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "we don't have a chat in room");
                            return;
                        }
                        for (QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            boolean isSeen = (boolean) documentSnapshot.get("isSeen");
                            Log.d(TAG, "isSeen: "+isSeen);
                            Log.d(TAG, "chat.isSeen(): "+chat.isSeen());

                            if (chat.getIdSend().equals(myId)){
                                holder.txtLastChat.setTypeface(Typeface.DEFAULT);
                                holder.txtLastChat.setTextColor(context.getResources().getColor(R.color.colorTextDefault));
                                holder.txtRoomName.setTypeface(Typeface.DEFAULT);
                            }else {
                                if (isSeen){
                                    holder.txtLastChat.setTypeface(Typeface.DEFAULT);
                                    holder.txtLastChat.setTextColor(context.getResources().getColor(R.color.colorTextDefault));
                                    holder.txtRoomName.setTypeface(Typeface.DEFAULT);
                                }else {
                                    holder.txtLastChat.setTypeface(Typeface.DEFAULT_BOLD);
                                    holder.txtLastChat.setTextColor(context.getResources().getColor(R.color.colorTextBlack));
                                    holder.txtRoomName.setTypeface(Typeface.DEFAULT_BOLD);
                                }
                            }
                            holder.txtLastChat.setText(chat.getContent());
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return listRoom.size();
    }

    public class ViewHolderRoom extends RecyclerView.ViewHolder{
        CircleImageView avatar,status;
        TextView txtRoomName,txtLastChat;
        public ViewHolderRoom(@NonNull final View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatarRoom);
            txtRoomName=itemView.findViewById(R.id.textViewRoomName);
            txtLastChat = itemView.findViewById(R.id.textViewLastChat);
            status = itemView.findViewById(R.id.statusRoomChat);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mlistener!=null){
                        mlistener.onClick(itemView,getLayoutPosition());
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
}
