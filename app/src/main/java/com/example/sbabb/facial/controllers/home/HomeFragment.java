package com.example.sbabb.facial.controllers.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sbabb.facial.R;
import com.example.sbabb.facial.controllers.addperson.AddPersonActivity;
import com.example.sbabb.facial.controllers.takepicture.TakePictureFragment;
import com.example.sbabb.facial.controllers.viewperson.ViewPersonActivity;
import com.example.sbabb.facial.controllers.viewperson.ViewPersonFragment;

import java.io.File;

public class HomeFragment extends Fragment {

    private static final int REQUEST_PHOTO = 0;

    private FloatingActionButton mAddPersonFAButton;
    private FloatingActionButton mTakePictureFAButton;
    private RecyclerView mPersonRecyclerView;

    private File mPhotoFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhotoFile = new File("IMGTemp_.jpg");

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
                Intent i = AddPersonActivity.newIntent(getContext(), null);
            }
        });

        mTakePictureFAButton = (FloatingActionButton) v.findViewById(R.id.take_picture_button);
        mTakePictureFAButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePictureFragment fragment = TakePictureFragment.newInstance(Uri.fromFile(mPhotoFile));
                FragmentManager fm = getChildFragmentManager();
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        });

        updateUI();
        return v;
    }

    public void updateUI() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PHOTO) {
            Uri photoUri = data.getParcelableExtra(TakePictureFragment.EXTRA_PHOTO);
            // decide if its a person
            //if its not, display some feedback
            // if it is, decide who it is
            // get their personKey
            // view them in a new PersonViewFragment
        }
    }

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
