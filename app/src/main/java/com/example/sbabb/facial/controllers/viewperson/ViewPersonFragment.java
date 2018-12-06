package com.example.sbabb.facial.controllers.viewperson;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sbabb.facial.R;
import com.example.sbabb.facial.controllers.addperson.AddPersonFragment;
import com.example.sbabb.facial.model.ModelManager;
import com.example.sbabb.facial.model.Person;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

// from "take picture button" or library
// takes in bitmap of person's face
// if bitmap is null (navigated to by "take pic but), sends intent to camera to get picture
// uses faceID to find whose face it is
// displays their info in a viewing window
public class ViewPersonFragment extends Fragment {
    public static final String TAG = "ViewPersonFragment";

    private static final String ARG_PERSON = "argPersonn";

    private ImageView mPersonImageView;
    private Button mEditButton;
    private TextView mNameTextView;

    private ModelManager mm = ModelManager.get(getContext());
    private String mPersonKey;
    private Person mPerson;
    private File mPhotoFile;

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

        mPersonImageView = (ImageView) v.findViewById(R.id.person_main_image);
        mNameTextView = (TextView) v.findViewById(R.id.person_name_tv);

        mEditButton = v.findViewById(R.id.edit_person_but);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPersonFragment.newInstance(mPersonKey);
            }
        });

        updateUI();
        return v;
    }

    public void updateUI() {
        if (mPerson != null) {
            mNameTextView.setText(mPerson.getName());
        }
        if (mPhotoFile != null) {
            mPersonImageView.setImageURI(Uri.fromFile(mPhotoFile));
        }
    }

    private void retrievePerson() {
        mm.getPersonRef(mPersonKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "successfully retrieved person.");
                mPerson = dataSnapshot.getValue(Person.class);
                updateUI();
                mPhotoFile = mm.getPhotoFile(mPerson, 1);
                mm.getPersonImage1Ref(mPerson).getFile(mPhotoFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Retrieved photo 1.");
                        updateUI();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to retrieve photo1.");
                    }
                });
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
