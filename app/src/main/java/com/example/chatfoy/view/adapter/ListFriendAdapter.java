package com.example.chatfoy.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatfoy.R;
import com.example.chatfoy.model.object.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListFriendAdapter extends RecyclerView.Adapter {
    private ArrayList<User> listFriend;
    private Context context;
    FirebaseAuth firebaseAuth =FirebaseAuth.getInstance();

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
        if (user.getUrlAvatar().equals("default")){
            holderFriend.circleImageView.setImageResource(R.mipmap.ic_logo);
        }else {
            Glide.with(context).load(user.getUrlAvatar()).into(holderFriend.circleImageView);
        }
        holderFriend.txtFullName.setText(user.getFullName());

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
