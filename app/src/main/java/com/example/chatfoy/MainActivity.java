package com.example.chatfoy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.chatfoy.fragment.ListRequestFragment;
import com.example.chatfoy.fragment.ListFriendFragment;
import com.example.chatfoy.fragment.MessageFragment;
import com.example.chatfoy.fragment.ProfileFragment;
import com.example.chatfoy.fragment.SearchFragment;
import com.example.chatfoy.model.object.RoomChat;
import com.example.chatfoy.model.object.ShareViewModel;
import com.example.chatfoy.model.object.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AAA";
    Toolbar toolbar;
    FrameLayout frameLayout;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refFriend = db.collection("friends");
    CollectionReference refRequest = db.collection("friendRequest");
    CollectionReference refRoom = db.collection("listRoom");
    CollectionReference refUser = db.collection("users");
    String myId = firebaseAuth.getCurrentUser().getUid();
    ArrayList<User> lisFriend = new ArrayList<>();
    ArrayList<User> listRequest = new ArrayList<>();
    ArrayList<RoomChat> listRoom = new ArrayList<>();
    ShareViewModel viewModel ;
    boolean userIsOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FindView();
        viewModel = new ViewModelProvider(this).get(ShareViewModel.class);
        //Set up toolbar
        setSupportActionBar(toolbar);
        //Set up bottom navigation
        getSupportActionBar().setTitle("Message");
        LoadFragment(new MessageFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.message:
                        fragment = new MessageFragment();
                        toolbar.setTitle("Message");
                        LoadFragment(fragment);
                        return true;
                    case R.id.listFriend:
                        fragment = new ListFriendFragment();
                        toolbar.setTitle("ListFirend");
                        LoadFragment(fragment);
                        return true;
                    case R.id.addFriend:
                        fragment = new ListRequestFragment();
                        toolbar.setTitle("AddFriend");
                        LoadFragment(fragment);
                        return true;
                    case R.id.search:
                        fragment = new SearchFragment();
                        toolbar.setTitle("Search");
                        LoadFragment(fragment);
                        return true;
                    case R.id.profile:
                        fragment = new ProfileFragment();
                        toolbar.setTitle("Profile");
                        LoadFragment(fragment);
                        return true;
                }
                return false;
            }
        });

        userIsOnline = true;



        LoadListRoom();
        LoadListFriend();
        LoadListRequest();

    }

    private void LoadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayoutMain, fragment);
        transaction.commit();
    }

    private void FindView() {
        toolbar = findViewById(R.id.toolbarMain);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayoutMain);

    }
    private void LoadListFriend(){
        refFriend.document(myId).collection("listFriend")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e!= null ){
                            Log.d(TAG, "Fail to load list friend");
                            return;
                        }
                        lisFriend.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            User user = documentSnapshot.toObject(User.class);
                            lisFriend.add(user);
                        }
                        viewModel.getDataListFriend().setValue(lisFriend);
                        Log.d(TAG, "onEvent:Had list");
                    }
                });
    }
    private void LoadListRequest(){
        refRequest.document(myId).collection("listRequest")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e!= null ){
                            Log.d(TAG, "Fail to load list request");
                            return;
                        }
                        listRequest.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            User user = documentSnapshot.toObject(User.class);
                            listRequest.add(user);
                        }
                        viewModel.getDataListRequest().setValue(listRequest);
                        Log.d(TAG, "onEvent: Had list request");
                    }
                });
    }
    private void LoadListRoom(){
        refRoom.whereArrayContains("listIdMember",myId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e!=null){
                            Log.d(TAG, "Fail to load list room");
                        }
                        listRoom.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            RoomChat roomChat = documentSnapshot.toObject(RoomChat.class);
                            listRoom.add(roomChat);
                        }
                        viewModel.getDataListRoom().setValue(listRoom);
                        Log.d(TAG, "Had list room");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refUser.document(myId).update("online",true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        refUser.document(myId).update("online",false);
    }
}
