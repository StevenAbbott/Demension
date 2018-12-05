package com.example.sbabb.facial.controllers.viewperson;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sbabb.facial.R;
import com.example.sbabb.facial.model.ModelManager;
import com.example.sbabb.facial.model.Person;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// from "take picture button" or library
// takes in bitmap of person's face
// if bitmap is null (navigated to by "take pic but), sends intent to camera to get picture
// uses faceID to find whose face it is
// displays their info in a viewing window
public class ViewPersonFragment extends Fragment {
    public static final String TAG = "ViewPersonFragment";

    private static final String ARG_PERSON = "argPersonn";

    private ModelManager mm = ModelManager.get(getContext());
    private String mPersonKey;
    private Person mPerson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mPersonKey = savedInstanceState.getString(ARG_PERSON);
        }

        retrievePerson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_person, container, false);




        updateUI();
        return v;
    }

    public void updateUI() {

    }

    private void retrievePerson() {
        mm.getPersonRef(mPersonKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "successfully retrieved person.");
                mPerson = dataSnapshot.getValue(Person.class);
                updateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to retrieve person.");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(ARG_PERSON, mPersonKey);
    }


    public static ViewPersonFragment newInstance(String personKey) {
        if (personKey == null) {
            throw new IllegalArgumentException("personKey cannot equal null");
        }
        Bundle args = new Bundle();
        ViewPersonFragment fragment = new ViewPersonFragment();
        args.putString(ARG_PERSON, personKey);
        fragment.setArguments(args);
        return fragment;
    }
}
