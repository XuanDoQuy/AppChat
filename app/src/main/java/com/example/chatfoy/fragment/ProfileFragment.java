package com.example.chatfoy.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.chatfoy.LoginActivity;
import com.example.chatfoy.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    Button btnSignOut;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        btnSignOut = view.findViewById(R.id.buttonSignOut);
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }

}
