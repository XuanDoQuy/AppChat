package com.example.chatfoy;
/*
êccêcc
nknk
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chatfoy.model.object.Chat;
import com.example.chatfoy.model.object.RoomChat;
import com.example.chatfoy.model.object.User;
import com.example.chatfoy.view.adapter.ListChatAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    CircleImageView avatarTB,statusTB;
    TextView txtRoomNameTB;
    TextView txtStatusTB;
    EditText edtChatInput;
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageButton btnSend;
    ListChatAdapter adapter;
    ArrayList<Chat> listChat = new ArrayList<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String myId = firebaseAuth.getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refUser = db.collection("users");
    CollectionReference refRoomChat = db.collection("listRoom");
    ListenerRegistration chatListener, seenListener;
    RoomChat roomChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        FindView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        roomChat = (RoomChat) bundle.getSerializable("roomChat");
        UpdateUI();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edtChatInput.getText().toString();
                Map<String, Object> map = new HashMap<>();
                map.put("idSend", myId);
                map.put("content", content);
                map.put("isSeen",false);
                map.put("time", FieldValue.serverTimestamp());
                refRoomChat.document(roomChat.getRoomId()).collection("chats").add(map);
                edtChatInput.setText("");
            }
        });
    }

    private void UpdateUI() {
        ArrayList<String> listIdMember = roomChat.getListIdMember();

        for (int i = 0; i < listIdMember.size(); i++) {
            final String userId = listIdMember.get(i);
            if (!userId.equals(myId)) {
                refUser.document(userId)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                if (e != null) return;
                                User user = documentSnapshot.toObject(User.class);
                                boolean isOnline = user.isOnline();
                                if (isOnline) {
                                    txtStatusTB.setText("Online");
                                } else {
                                    txtStatusTB.setText("Offline");
                                }
                                if (user.getUrlAvatar().equals("default")){
                                    avatarTB.setImageResource(R.mipmap.ic_logo);
                                }else {
                                    Glide.with(getApplicationContext()).load(user.getUrlAvatar()).into(avatarTB);
                                }
                                txtRoomNameTB.setText(user.getFullName());
                                if (user.isOnline()){
                                    statusTB.setVisibility(View.VISIBLE);
                                }else {
                                    statusTB.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LoadChats();
        //refUser.document(myId).update("online",true);
    }


    @Override
    protected void onStop() {
        super.onStop();
        //refUser.document(myId).update("online",false);
    }
    @Override
    protected void onPause() {
        super.onPause();
        chatListener.remove();
        seenListener.remove();
    }



    private void LoadChats() {
        chatListener = refRoomChat.document(roomChat.getRoomId()).collection("chats")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) return;
                        listChat.clear();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            boolean isSeen = (boolean) documentSnapshot.get("isSeen");
                            String idSend = (String) documentSnapshot.get("idSend");
                            String content = (String) documentSnapshot.get("content");
                            Chat chat = new Chat(idSend,content,isSeen);
                            listChat.add(chat);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        seenListener = refRoomChat.document(roomChat.getRoomId()).collection("chats")
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e!=null) return;
                        if (queryDocumentSnapshots.isEmpty()) return;
                        for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                            DocumentSnapshot documentSnapshot = documentChange.getDocument();
                            switch (documentChange.getType()){
                                case ADDED:
                                    Chat chat = documentSnapshot.toObject(Chat.class);
                                    if (!chat.getIdSend().equals(myId)){
                                        String chatId = documentSnapshot.getId();
                                        refRoomChat.document(roomChat.getRoomId()).collection("chats")
                                                .document(chatId).update("isSeen",true);
                                    }
                                    break;
                            }
                        }
                    }
                });
    }


    private void FindView() {
        toolbar = findViewById(R.id.toolbarChat);
        avatarTB = findViewById(R.id.avatarToolBarChat);
        txtRoomNameTB = findViewById(R.id.roomNameToolBarChat);
        txtStatusTB = findViewById(R.id.statusToolBarChat);
        statusTB = findViewById(R.id.imgStatusChat);
        edtChatInput = findViewById(R.id.chatInput);
        btnSend = findViewById(R.id.imgBtnSend);
        recyclerView = findViewById(R.id.recyclerViewChat);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ListChatAdapter(getApplicationContext(), listChat);
        recyclerView.setAdapter(adapter);


    }
}
