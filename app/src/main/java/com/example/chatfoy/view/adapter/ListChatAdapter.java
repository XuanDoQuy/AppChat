package com.example.chatfoy.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatfoy.R;
import com.example.chatfoy.model.object.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListChatAdapter extends RecyclerView.Adapter {

    private static final int LEFT = 100;
    private static final int RIGHT = 101;

    private Context context;
    private ArrayList<Chat> listChat;
    String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refUser = db.collection("users");

    public ListChatAdapter(Context context, ArrayList<Chat> listChat) {
        this.context = context;
        this.listChat = listChat;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case LEFT:
                View viewLeft = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false);
                RecyclerView.ViewHolder viewHolderLeft = new ViewHolderLeft(viewLeft);
                return viewHolderLeft;
            case RIGHT:
                View viewRight = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false);
                RecyclerView.ViewHolder viewHolderRight = new ViewHolderRight(viewRight);
                return viewHolderRight;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        final Chat chat = listChat.get(position);
        if (holder.getItemViewType()==LEFT){
            final ViewHolderLeft viewHolderLeft = (ViewHolderLeft) holder;
            refUser.document(chat.getIdSend()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (!task.isSuccessful()) return;
                    String urlAvatar = (String) task.getResult().get("urlAvatar");
                    if (position==0){
                        viewHolderLeft.avatar.setVisibility(View.VISIBLE);
                        if (urlAvatar.equals("default")){
                            viewHolderLeft.avatar.setImageResource(R.mipmap.ic_logo);
                        }else {
                            Glide.with(context).load(urlAvatar).into(viewHolderLeft.avatar);
                        }
                    }else {
                        Chat preChat = listChat.get(position-1);
                        if (preChat.getIdSend().equals(myId)){
                            viewHolderLeft.avatar.setVisibility(View.VISIBLE);
                            if (urlAvatar.equals("default")){
                                viewHolderLeft.avatar.setImageResource(R.mipmap.ic_logo);
                            }else {
                                Glide.with(context).load(urlAvatar).into(viewHolderLeft.avatar);
                            }
                        }else {
                            viewHolderLeft.avatar.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });

            viewHolderLeft.content.setText(chat.getContent());

        }else {
            ViewHolderRight viewHolderRight = (ViewHolderRight) holder;
            viewHolderRight.content.setText(chat.getContent());
            if (position == 0){
                viewHolderRight.txtSeen.setVisibility(View.VISIBLE);
                if (chat.isSeen()){
                    viewHolderRight.txtSeen.setText("Seen");
                }else {
                    viewHolderRight.txtSeen.setText("Delivered");
                }
            }else {
                viewHolderRight.txtSeen.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }

    public class ViewHolderLeft extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView content;

        public ViewHolderLeft(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatarLeft);
            content = itemView.findViewById(R.id.contentChatLeft);
        }
    }

    public class ViewHolderRight extends RecyclerView.ViewHolder {
        TextView content;
        TextView txtSeen;
        public ViewHolderRight(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.contentChatRight);
            txtSeen = itemView.findViewById(R.id.textViewSeen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Chat chat = listChat.get(position);
        if (chat.getIdSend().equals(myId)) return RIGHT;
        else return LEFT;
    }
}
