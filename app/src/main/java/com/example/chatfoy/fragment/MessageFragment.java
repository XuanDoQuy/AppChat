package com.example.chatfoy.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.chatfoy.view.adapter.ListRoomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    private static final String TAG = "AAA";
    RecyclerView recyclerView;
    ArrayList<RoomChat> listRoom;
    ListRoomAdapter adapter;
    String myId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refRoom = db.collection("listRoom");
    CollectionReference refUser = db.collection("users");
    ShareViewModel viewModel;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);
        recyclerView = view.findViewById(R.id.recyclerViewRoom);
        listRoom = new ArrayList<>();

        LoadListRoom();
        return view;
    }

    private void LoadListRoom() {
        viewModel.getDataListRoom().observe(getViewLifecycleOwner(), new Observer<ArrayList<RoomChat>>() {
            @Override
            public void onChanged(final ArrayList<RoomChat> roomChats) {
                adapter = new ListRoomAdapter(getActivity().getApplicationContext(), roomChats);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new ListRoomAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        RoomChat roomChat = roomChats.get(position);
                        Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("roomChat",roomChat);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
