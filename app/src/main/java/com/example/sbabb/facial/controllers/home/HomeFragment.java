package com.example.sbabb.facial.controllers.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sbabb.facial.R;
import com.example.sbabb.facial.controllers.addperson.AddPersonActivity;

public class HomeFragment extends Fragment {

    FloatingActionButton mAddPersonFAButton;
    FloatingActionButton mTakePictureFAButton;
    RecyclerView mPersonRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        mPersonRecyclerView = (RecyclerView) v.findViewById(R.id.people_recycler_view);

        mAddPersonFAButton = (FloatingActionButton) v.findViewById(R.id.add_person_button);
        mAddPersonFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = AddPersonActivity.newIntent(getActivity(), true);
            }
        });

        mTakePictureFAButton = (FloatingActionButton) v.findViewById(R.id.take_picture_button);
        mTakePictureFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // take picture
                // identify picture
                // start ViewPersonFragment
            }
        });

        updateUI();
        return v;
    }

    public void updateUI() {

    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
