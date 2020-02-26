package com.example.chatfoy.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatfoy.ChatActivity;
import com.example.chatfoy.R;
import com.example.chatfoy.model.object.RoomChat;
import com.example.chatfoy.model.object.ShareViewModel;
import com.example.chatfoy.model.object.User;
import com.example.chatfoy.view.adapter.ListFriendAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFriendFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<User> listFriend ;
    ListFriendAdapter listFriendAdapter;
    ShareViewModel viewModel;
    String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refUser = db.collection("users");
    CollectionReference refRoomChat = db.collection("listRoom");
    User currentUser;
    Map<String,Object> curMap = new HashMap<>();
    private static final String TAG ="AAA";
    public ListFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_friend, container, false);
        recyclerView= view.findViewById(R.id.recyclerViewListFriend);
        viewModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);

        refUser.document(myId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);

            }
        });

        viewModel.getDataListFriend().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(final ArrayList<User> users) {
                Log.d(TAG, "Had list friend from view model");
                listFriendAdapter = new ListFriendAdapter(users,getActivity().getApplicationContext());
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                recyclerView.setAdapter(listFriendAdapter);
                listFriendAdapter.setOnItemClickListener(new ListFriendAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        final Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                        User user = users.get(position);
                        final String roomId = creatRoomId(myId,user.getUserId());
                        ArrayList<String> listIdMember = new ArrayList<>();
                        listIdMember.add(myId);
                        listIdMember.add(user.getUserId());
                        final RoomChat roomChat = new RoomChat(roomId,listIdMember,0);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("roomChat",roomChat);
                        intent.putExtras(bundle);
                        refRoomChat.whereEqualTo("roomId",roomId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (queryDocumentSnapshots.isEmpty()){
                                            refRoomChat.document(roomId).set(roomChat);

                                        }
                                    }
                                });
                        startActivity(intent);
                    }
                });
            }
        });

        return view;
    }
    private String creatRoomId(String myId,String userId){
        String roomId;
        if (myId.compareTo(userId)>0){
            roomId = myId+"_"+userId;
        }else {
            roomId = userId+"_"+userId;
        }
        return roomId;
    }

}
