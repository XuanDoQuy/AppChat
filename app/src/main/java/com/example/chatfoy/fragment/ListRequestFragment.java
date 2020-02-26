package com.example.chatfoy.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatfoy.R;
import com.example.chatfoy.model.object.ShareViewModel;
import com.example.chatfoy.model.object.User;
import com.example.chatfoy.view.adapter.ListRequestAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListRequestFragment extends Fragment {
    RecyclerView recyclerView;
    ShareViewModel viewModel;
    ListRequestAdapter adapter;

    public ListRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_request, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewListRequest);
        viewModel = new ViewModelProvider(getActivity()).get(ShareViewModel.class);
        viewModel.getDataListRequest().observe(getViewLifecycleOwner(), new Observer<ArrayList<User>>() {
            @Override
            public void onChanged(ArrayList<User> users) {
                adapter = new ListRequestAdapter(getActivity().getApplicationContext(),users);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                recyclerView.setAdapter(adapter);
            }
        });
        return view;
    }

}
