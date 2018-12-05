package com.example.sbabb.facial.controllers.addperson;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

// from "add new person button" or "edit person"
// takes in person
// if person is not null, editable fields are populated
// if person is null (navigated to from "add new person but"), displays blank imageView and fields
// when the user exits/ presses save, it will update the image and Person in FB
public class AddPersonFragment extends Fragment{
    public static final String TAG = "AddPersonFragment";

    private static final int REQUEST_PHOTO_1 = 0;
    private static final int REQUEST_PHOTO_2 = 0;
    private static final int REQUEST_PHOTO_3 = 0;

    public static final String ARG_PERSON_KEY = "person_key";

    private ImageView mAddPhotoImageView1;
    private ImageView mAddPhotoImageView2;
    private ImageView mAddPhotoImageView3;
    private EditText mNameEditText;
    private Button mSavePersonButton;

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

        if (savedInstanceState != null) {
            mPersonKey = savedInstanceState.getString(ARG_PERSON_KEY);
        }

        mPhotoFile1 = mm.getPhotoFile(mPerson, 1);
        mPhotoFile2 = mm.getPhotoFile(mPerson, 2);
        mPhotoFile3 = mm.getPhotoFile(mPerson, 3);

        mNewPerson = (mPersonKey == null);
        if (mPersonKey != null) {
            mm.getPersonRef(mPersonKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mPerson = dataSnapshot.getValue(Person.class);
                    Log.d(TAG, "Successfully retrieved person from database.");
                    retrieveImages();
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

        mSavePersonButton = (Button) v.findViewById(R.id.save_person_button);
        mSavePersonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mPhotoFile1 != null) && (mPhotoFile2 != null) && (mPhotoFile3 != null) && (mNameEditText.getText().toString() != null)) {
                    mPerson.setName(mNameEditText.getText().toString());
                    uploadImagesToStorage();
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
                TakePictureFragment.newInstance(Uri.fromFile(mPhotoFile1));
            }
        });

        mAddPhotoImageView2 = (ImageView) v.findViewById(R.id.add_photo_2_iv);
        mAddPhotoImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePictureFragment.newInstance(Uri.fromFile(mPhotoFile2));
            }
        });

        mAddPhotoImageView3 = (ImageView) v.findViewById(R.id.add_photo_3_iv);
        mAddPhotoImageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePictureFragment.newInstance(Uri.fromFile(mPhotoFile3));
            }
        });

        updateUI();
        return v;
    }

    public void updateUI() {
        mAddPhotoImageView1.setImageURI(Uri.fromFile(mPhotoFile1));
        mAddPhotoImageView2.setImageURI(Uri.fromFile(mPhotoFile2));
        mAddPhotoImageView3.setImageURI(Uri.fromFile(mPhotoFile3));
        mNameEditText.setText(mPerson.getName());
    }

    public void retrieveImages() {
        mm.getPersonImage1Ref(mPerson).getFile(mPhotoFile1).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
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

        mm.getPersonImage2Ref(mPerson).getFile(mPhotoFile2).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Retrieved photo 2.");
                updateUI();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to retrieve photo2.");
            }
        });

        mm.getPersonImage3Ref(mPerson).getFile(mPhotoFile3).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                updateUI();
                Log.d(TAG, "Retrieved photo 3.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to retrieve photo3.");
            }
        });
    }

    private void uploadImagesToStorage() {
        Uri file1 = Uri.fromFile(mPhotoFile1);
        UploadTask uploadTask1 = mm.getPersonImage1Ref(mPerson).putFile(file1);
        uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Successfully uploaded photo1.");
                Uri file2 = Uri.fromFile(mPhotoFile2);
                UploadTask uploadTask2 = mm.getPersonImage1Ref(mPerson).putFile(file2);
                uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Successfully uploaded photo2.");
                        Uri file3 = Uri.fromFile(mPhotoFile3);
                        UploadTask uploadTask3 = mm.getPersonImage1Ref(mPerson).putFile(file3);
                        uploadTask3.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d(TAG, "Successfully uploaded photo3.");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Failed to upload photo3.");
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to upload photo2.");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Failed to upload photo1.");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PHOTO_1) {
            mPhotoFile1 = new File(((Uri) data.getParcelableExtra(TakePictureFragment.EXTRA_PHOTO)).toString());
        } else if (requestCode == REQUEST_PHOTO_2){
            mPhotoFile2 = new File(((Uri) data.getParcelableExtra(TakePictureFragment.EXTRA_PHOTO)).toString());
        } else if (requestCode == REQUEST_PHOTO_3) {
            mPhotoFile3 = new File(((Uri) data.getParcelableExtra(TakePictureFragment.EXTRA_PHOTO)).toString());
        }

        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(ARG_PERSON_KEY, mPersonKey);
    }

    public static AddPersonFragment newInstance(String personKey) {
        Bundle args = new Bundle();
        args.putString(ARG_PERSON_KEY, personKey);
        AddPersonFragment fragment = new AddPersonFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
