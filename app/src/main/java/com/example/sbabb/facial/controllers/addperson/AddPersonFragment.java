package com.example.sbabb.facial.controllers.addperson;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sbabb.facial.R;
import com.example.sbabb.facial.controllers.takepicture.TakePictureFragment;
import com.example.sbabb.facial.model.ModelManager;
import com.example.sbabb.facial.model.Person;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;

// from "add new person button" or "edit person"
// takes in person
// if person is not null, editable fields are populated
// if person is null (navigated to from "add new person but"), displays blank imageView and fields
// when the user exits/ presses save, it will update the image and Person in FB
public class AddPersonFragment extends Fragment{
    public static final String TAG = "AddPersonFragment";

    private static final int REQUEST_PHOTO_1 = 0;
    private static final int REQUEST_PHOTO_2 = 1;
    private static final int REQUEST_PHOTO_3 = 2;

    public static final String ARG_PERSON_KEY = "person_key";
    public static final String ARG_NEW_PERSON = "new_person";

    private ImageView mAddPhotoImageView1;
    private ImageView mAddPhotoImageView2;
    private ImageView mAddPhotoImageView3;
    private EditText mNameEditText;
    private Button mSavePersonButton;
    private ProgressBar mProgressBar;

    private boolean mImage1Loading;
    private boolean mImage2Loading;
    private boolean mImage3Loading;

    private ModelManager mm;
    private String mPersonKey;
    private boolean mNewPerson;
    private Person mPerson;
    private File mPhotoFile1;
    private File mPhotoFile2;
    private File mPhotoFile3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mm = ModelManager.get(getContext());

        mPersonKey = getArguments().getString(ARG_PERSON_KEY);
        mImage1Loading = false;
        mImage2Loading = false;
        mImage3Loading = false;
        mNewPerson = (mPersonKey == null);

        if (savedInstanceState != null) {
            mPersonKey = savedInstanceState.getString(ARG_PERSON_KEY);
            mNewPerson = savedInstanceState.getBoolean(ARG_PERSON_KEY);
        }

        if (mPersonKey != null) {
            mm.getPersonRef(mPersonKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mPerson = dataSnapshot.getValue(Person.class);
                    Log.d(TAG, "Successfully retrieved person from database.");
                    if (!mNewPerson) {
                        retrieveImages();
                    }
                    updateUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, "Failed to retrieve person from database.");
                }
            });
        } else {
            mPersonKey = mm.generatePersonKey();
            mPerson = new Person(mPersonKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_person, container, false);

        mNameEditText = (EditText) v.findViewById(R.id.enter_name_et);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        mSavePersonButton = (Button) v.findViewById(R.id.save_person_button);
        mSavePersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mPhotoFile1 != null) && (mPhotoFile2 != null) && (mPhotoFile3 != null) && (mNameEditText.getText().toString() != null)) {
                    mPerson.setName(mNameEditText.getText().toString());
                    uploadImagesToStorage();
                    mm.getPersonRef(mPersonKey).setValue(mPerson);
                    if (mNewPerson) {
                        // add new person to azure personGroup
                    }
                    // update azure person
                } else {
                    Log.d(TAG, "Not all 3 pictures and a name were entered.");
                    Toast.makeText(getContext(), "Ensure all 3 pictures and a name have been entered.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAddPhotoImageView1 = (ImageView) v.findViewById(R.id.add_photo_1_iv);
        mAddPhotoImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePictureFragment fragment = TakePictureFragment.newInstance(mPerson.getPhotoFilename(1));
                fragment.setTargetFragment(AddPersonFragment.this, REQUEST_PHOTO_1);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        });

        mAddPhotoImageView2 = (ImageView) v.findViewById(R.id.add_photo_2_iv);
        mAddPhotoImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePictureFragment fragment = TakePictureFragment.newInstance(mPerson.getPhotoFilename(2));
                fragment.setTargetFragment(AddPersonFragment.this, REQUEST_PHOTO_2);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        });

        mAddPhotoImageView3 = (ImageView) v.findViewById(R.id.add_photo_3_iv);
        mAddPhotoImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePictureFragment fragment = TakePictureFragment.newInstance(mPerson.getPhotoFilename(3));
                fragment.setTargetFragment(AddPersonFragment.this, REQUEST_PHOTO_3);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .add(R.id.fragment_container, fragment)
                        .commit();
            }
        });

        updateUI();
        return v;
    }

    public void updateUI() {
        if (mPerson.getName() != null) {
            mNameEditText.setText(mPerson.getName());
        }
    }

    public void updateImage1() {
        mPhotoFile1 = mm.getPhotoFile(mPerson, 1);
        mAddPhotoImageView1.setRotation(90);
        mAddPhotoImageView1.setImageURI(Uri.fromFile(mPhotoFile1));
    }

    public void updateImage2() {
        mPhotoFile2 = mm.getPhotoFile(mPerson, 2);
        mAddPhotoImageView2.setRotation(90);
        mAddPhotoImageView2.setImageURI(Uri.fromFile(mPhotoFile2));
    }

    public void updateImage3() {
        mPhotoFile3 = mm.getPhotoFile(mPerson, 3);
        mAddPhotoImageView3.setRotation(90);
        mAddPhotoImageView3.setImageURI(Uri.fromFile(mPhotoFile3));
    }

    public void retrieveImages() {
        mProgressBar.setVisibility(View.VISIBLE);
        mImage1Loading = true;
        mImage2Loading = true;
        mImage3Loading = true;
        mm.getPersonImage1Ref(mPerson).getFile(mPhotoFile1).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Retrieved photo 1.");
                mImage1Loading = false;
                updateImage1();
                updateProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to retrieve photo1.");
                mImage1Loading = false;
                updateProgressBar();
            }
        });

        mm.getPersonImage2Ref(mPerson).getFile(mPhotoFile2).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Retrieved photo 2.");
                updateImage2();
                mImage2Loading = false;
                updateProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to retrieve photo2.");
                mImage2Loading = false;
                updateProgressBar();
            }
        });

        mm.getPersonImage3Ref(mPerson).getFile(mPhotoFile3).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Retrieved photo 3.");
                updateImage3();
                mImage3Loading = false;
                updateProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to retrieve photo3.");
                mImage3Loading = false;
                updateProgressBar();
            }
        });
    }

    private void uploadImagesToStorage() {
        mProgressBar.setVisibility(View.VISIBLE);
        mImage1Loading = true;
        mImage2Loading = true;
        mImage3Loading = true;
        Uri file1 = Uri.fromFile(mPhotoFile1);
        final UploadTask uploadTask1 = mm.getPersonImage1Ref(mPerson).putFile(file1);
        uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Successfully uploaded photo1.");
                mImage1Loading = false;
                updateProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to upload photo1.");
                mImage1Loading = false;
                updateProgressBar();
            }
        });

        Uri file2 = Uri.fromFile(mPhotoFile2);
        UploadTask uploadTask2 = mm.getPersonImage2Ref(mPerson).putFile(file2);
        uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Successfully uploaded photo2.");
                mImage2Loading = false;
                updateProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to upload photo2.");
                mImage2Loading = false;
                updateProgressBar();
            }
        });


        Uri file3 = Uri.fromFile(mPhotoFile3);
        UploadTask uploadTask3 = mm.getPersonImage3Ref(mPerson).putFile(file3);
        uploadTask3.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Successfully uploaded photo3.");
                mImage3Loading = false;
                updateProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to upload photo3.");
                mImage3Loading = false;
                updateProgressBar();
            }
        });
    }

    public void updateProgressBar() {
        mProgressBar.setVisibility((mImage1Loading || mImage2Loading || mImage3Loading) ?
                View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PHOTO_1) {
            updateImage1();
        } else if (requestCode == REQUEST_PHOTO_2){
            updateImage2();
        } else if (requestCode == REQUEST_PHOTO_3) {
            updateImage3();
        }

        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(ARG_PERSON_KEY, mPersonKey);
        savedInstanceState.putBoolean(ARG_NEW_PERSON, mNewPerson);
    }

    public static AddPersonFragment newInstance(String personKey) {
        Bundle args = new Bundle();
        args.putString(ARG_PERSON_KEY, personKey);
        AddPersonFragment fragment = new AddPersonFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
