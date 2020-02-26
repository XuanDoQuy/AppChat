package com.example.chatfoy.fragment;


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
import android.widget.EditText;

import com.example.chatfoy.R;
import com.example.chatfoy.model.object.ShareViewModel;
import com.example.chatfoy.model.object.User;
import com.example.chatfoy.view.adapter.ListUserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "AAA";
    EditText editTextSearch;
    RecyclerView recyclerViewSearch;
    ArrayList<User> listUser;
    ListUserAdapter adapter;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference refUser = db.collection("users");
    CollectionReference refFriend = db.collection("friends");
    ArrayList<User> listFriend = new ArrayList<>();
    ArrayList<User> listRequest = new ArrayList<>();
    ShareViewModel viewModel;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        editTextSearch = view.findViewById(R.id.editTextSearch);
        recyclerViewSearch = view.findViewById(R.id.recyclerViewSearch);
        listUser = new ArrayList<>();
        adapter = new ListUserAdapter(getActivity().getApplicationContext(), listUser);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewSearch.setLayoutManager(layoutManager);
        recyclerViewSearch.setAdapter(adapter);
        viewModel = new ViewModelProvider(getActivity()).get(ShareViewModel.class);
        viewModel.getDataListFriend().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                listFriend.addAll(users);
                Log.d(TAG, "onChanged: " + users);
                viewModel.getDataListRequest().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
                    @Override
                    public void onChanged(ArrayList<User> users) {
                        listRequest.addAll(users);
                        Log.d(TAG, "onChanged: " + users);
//                        for (int i = 0; i < listRequest.size(); i++) {
//                            Log.d(TAG, listRequest.get(i).getEmail());
//                        }
                        LoadData();
                    }
                });
            }
        });


        return view;
    }

    private void LoadData() {
        refUser.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        return;
                    } else {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (!user.getUserId().equals(firebaseAuth.getCurrentUser().getUid())) {
                                boolean ans = listIsContains(listFriend,user);
                                boolean ans1 = listIsContains(listRequest,user);
                                for (int i = 0; i < listRequest.size(); i++) {
                                    Log.d(TAG, listRequest.get(i).getEmail());
                                }

                                if (!ans) {
                                    if (!ans1) {
                                        listUser.add(user);
                                        adapter.notifyItemInserted(listUser.size() - 1);
                                        Log.d(TAG, "onComplete: added user"+user.getFullName());
                                    }
                                }

                            }
                        }
                    }
                } else {
                    Log.d(TAG, "Fail to load all user ");
                    return;
                }
            }
        });
    }
    private boolean listIsContains(ArrayList<User> list, User user){
        for (int i=0;i<list.size();i++){
            if (list.get(i).getUserId().equals(user.getUserId())) return true;
        }
        return false;
    }


}
