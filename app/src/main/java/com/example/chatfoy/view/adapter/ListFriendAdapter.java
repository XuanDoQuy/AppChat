package com.example.chatfoy.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatfoy.R;
import com.example.chatfoy.model.object.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListFriendAdapter extends RecyclerView.Adapter {
    private static final String TAG = "AAA";
    private ArrayList<User> listFriend;
    private Context context;
    FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refFriend = db.collection("friends");
    CollectionReference refRequest = db.collection("friendRequest");
    CollectionReference refRoom = db.collection("listRoom");
    CollectionReference refUser = db.collection("users");
    String myId = firebaseAuth.getCurrentUser().getUid();

    private OnItemClickListener mlistener;

    public void setOnItemClickListener(OnItemClickListener mlistener){
        this.mlistener = mlistener;
    }

    public ListFriendAdapter(ArrayList<User> listFriend, Context context) {
        this.listFriend = listFriend;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_friend,parent,false);
        RecyclerView.ViewHolder viewHolder = new ViewHolderFriend(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final ViewHolderFriend holderFriend = (ViewHolderFriend) holder;
        User user = listFriend.get(position);
        refUser.document(user.getUserId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null){
                    Log.d( TAG, "onEvent: have on error in friend adapter");
                }else{
                    User user = value.toObject(User.class);
                    if (user.getUrlAvatar().equals("default")){
                        holderFriend.circleImageView.setImageResource(R.mipmap.ic_logo);
                    }else {
                        Glide.with(context).load(user.getUrlAvatar()).into(holderFriend.circleImageView);
                    }
                    holderFriend.txtFullName.setText(user.getFullName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFriend.size();
    }

    public class ViewHolderFriend extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView txtFullName;
        ConstraintLayout container;
        public ViewHolderFriend(@NonNull final View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.item_lf_avatar);
            txtFullName = itemView.findViewById(R.id.item_lf_fullname);
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
    public interface OnItemClickListener{
        void onClick(View view , int position);
    }
}
