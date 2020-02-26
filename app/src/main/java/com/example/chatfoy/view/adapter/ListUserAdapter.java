package com.example.chatfoy.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ViewHolderUser> {
    private static final String TAG = "AAA";
    private Context context;
    private ArrayList<User> listUser;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String myId = firebaseAuth.getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refRequest = db.collection("friendRequest");
    CollectionReference refUser = db.collection("users");
    User currentUser;

    public ListUserAdapter(Context context, ArrayList<User> listUser) {
        this.context = context;
        this.listUser = listUser;
        refUser.document(myId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "fail to load my account");
                    return;
                }
                currentUser = task.getResult().toObject(User.class);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderUser holder, int position) {
        final User user = listUser.get(position);
        if (user.getUrlAvatar().equals("default")) {
            holder.circleImageView.setImageResource(R.mipmap.ic_logo);
        } else {
            Glide.with(context).load(user.getUrlAvatar()).into(holder.circleImageView);
        }
        holder.textViewFullName.setText(user.getFullName());
        holder.buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refRequest.document(user.getUserId()).collection("listRequest")
                        .whereEqualTo("userId",myId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    if (task.getResult().isEmpty()){
                                        refRequest.document(user.getUserId()).collection("listRequest").document(myId).set(currentUser);
                                        Drawable drawable = context.getDrawable(R.drawable.ic_request_firend);
                                        holder.buttonAdd.setBackground(drawable);
                                    }else {
                                        Toast.makeText(context, "You have already submitted a request for this person", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });

            }
        });
        refRequest.document(user.getUserId()).collection("listRequest")
                .whereEqualTo("userId",myId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            if (task.getResult().isEmpty()){

                            }else {
                                Drawable drawable = context.getDrawable(R.drawable.ic_request_firend);
                                holder.buttonAdd.setBackground(drawable);
                            }
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolderUser extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textViewFullName;
        Button buttonAdd;

        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.item_user_avatar);
            textViewFullName = itemView.findViewById(R.id.item_user_fullname);
            buttonAdd = itemView.findViewById(R.id.item_user_btn_add);

        }
    }

}
