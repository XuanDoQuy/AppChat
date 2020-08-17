package com.example.chatfoy.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatfoy.LoginActivity;
import com.example.chatfoy.R;
import com.example.chatfoy.model.object.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "AAA";
    Button btnSignOut;
    CircleImageView imgAvatar;
    TextView txtName, txtDate, txtEmail;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    StorageReference refAvatar = firebaseStorage.getReference().child("avatar");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refUser = db.collection("users");
    DocumentReference refMainUser = refUser.document(firebaseAuth.getCurrentUser().getUid());
    StorageReference refCurrentUser = refAvatar.child(firebaseAuth.getCurrentUser().getUid());
    UploadTask uploadTask;
    AlertDialog dialog;
    private AlertDialog dialogLoading;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnSignOut = view.findViewById(R.id.buttonSignOut);
        imgAvatar = view.findViewById(R.id.imageAvatarProfile);
        txtName = view.findViewById(R.id.textViewProfileName);
        txtDate = view.findViewById(R.id.textViewProfileDate);
        txtEmail = view.findViewById(R.id.textViewProfileEmail);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                loading();
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickImageDialog.build(new PickSetup().setSystemDialog(true))
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                showDialog(r.getBitmap());
                            }
                        })
                        .show(getFragmentManager());
            }
        });
        refMainUser.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "onEvent: addSnapshotListener have an error");
                    return;
                } else {
                    User user = value.toObject(User.class);
                    txtName.setText("Họ tên: " + user.getFullName());
                    txtDate.setText("Giới tính: " + user.getSex());
                    txtEmail.setText("Email: " + user.getEmail());
                    if (!user.getUrlAvatar().equals("default")) Glide.with(getContext()).load(user.getUrlAvatar()).into(imgAvatar);
                }
            }
        });

        return view;
    }

    private void showDialog(final Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_avatar, null);
        builder.setView(view);
        ImageView img = view.findViewById(R.id.imageViewDiaglogChangeAvatar);
        final ProgressBar pb = view.findViewById(R.id.pBUploadImage);
        img.setImageBitmap(bitmap);
        pb.setMax(100);

        builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadTask.cancel();
                Log.d(TAG, "onClick: cancel");
            }
        });


        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] data = baos.toByteArray();
                upLoadImage(data, pb);
            }
        });

    }

    private void upLoadImage(byte[] data, final ProgressBar pb) {
        uploadTask = refCurrentUser.putBytes(data);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                long total = taskSnapshot.getTotalByteCount();
                long progress = taskSnapshot.getBytesTransferred();
                int percent = (int) (progress / total * 100);
                pb.setProgress(percent);
            }
        });
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (!task.isComplete()) {
                    Log.d(TAG, "onComplete: task be cancel");
                } else {
                    pb.setProgress(100);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    },2000);
                }
            }
        });
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return refCurrentUser.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    refUser.document(firebaseAuth.getCurrentUser().getUid()).update("urlAvatar", downloadUri.toString());
                    Log.d(TAG, "onComplete: " + downloadUri);
                } else {
                    Log.d(TAG, "onComplete: Upload failed");
                }
            }
        });

    }
    private void loading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_loading,null);
        builder.setView(view);
        ProgressBar pb = view.findViewById(R.id.pbLoading);
        pb.setIndeterminate(true);
        dialogLoading = builder.create();
        dialogLoading.setCanceledOnTouchOutside(false);
        dialogLoading.show();
        dialogLoading.getWindow().setLayout(200,200);
    }

}
