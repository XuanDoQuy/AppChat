package com.example.chatfoy.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatfoy.R;
import com.example.chatfoy.model.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListRequestAdapter extends RecyclerView.Adapter<ListRequestAdapter.ViewHolderRequest> {
    private Context context;
    private ArrayList<User> listRequest;
    String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db= FirebaseFirestore.getInstance();
    CollectionReference refFriend = db.collection("friends");
    CollectionReference refRequest = db.collection("friendRequest");
    CollectionReference refUser = db.collection("users");
    User currentUser;

    public ListRequestAdapter(Context context, ArrayList<User> listRequest) {
        this.context = context;
        this.listRequest = listRequest;
        refUser.document(myId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                currentUser = task.getResult().toObject(User.class);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolderRequest onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_request,parent,false);
        return new ViewHolderRequest(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderRequest holder, int position) {
        final User user = listRequest.get(position);
        if (user.getUrlAvatar().equals("default")){
            holder.circleImageView.setImageResource(R.mipmap.ic_logo);
        }else {
            Glide.with(context).load(user.getUrlAvatar()).into(holder.circleImageView);
        }
        holder.txtFullName.setText(user.getFullName());
        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refFriend.document(myId).collection("listFriend").document(user.getUserId()).set(user);
                refFriend.document(user.getUserId()).collection("listFriend").document(myId).set(currentUser);
                refRequest.document(myId).collection("listRequest").document(user.getUserId()).delete();
                holder.container.setVisibility(View.GONE);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refRequest.document(myId).collection("listRequest").document(user.getUserId()).delete();
                holder.container.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRequest.size();
    }

    public class ViewHolderRequest extends RecyclerView.ViewHolder{
        CircleImageView circleImageView;
        TextView txtFullName;
        Button btnConfirm,btnDelete;
        ConstraintLayout container;
        public ViewHolderRequest(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.item_lr_avatar);
            txtFullName = itemView.findViewById(R.id.item_lr_fullname);
            btnConfirm = itemView.findViewById(R.id.item_lr_btn_confirm);
            btnDelete = itemView.findViewById(R.id.item_lr_btn_delete);
            container = itemView.findViewById(R.id.item_lr_container);
        }
    }
}
