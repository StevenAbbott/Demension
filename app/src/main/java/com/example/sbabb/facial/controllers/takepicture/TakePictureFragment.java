package com.example.sbabb.facial.controllers.takepicture;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sbabb.facial.R;

import java.io.File;
import java.net.URI;
import java.util.List;

public class TakePictureFragment extends Fragment {
    private static String TAG = "TakePictureFragment";

    private static String ARG_PHOTO_NAME = "PhotoUri";
    public static final String EXTRA_PHOTO = "photoExtra";

    private static final int REQUEST_PHOTO = 0;

    private TextView mOutputTextView;

    private File mPhotoFile;
    private boolean mCanTakePhoto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mPhotoFile = new File(getContext().getFilesDir(), getArguments().getString(ARG_PHOTO_NAME));
        Log.d(TAG, "finally got that picture you asked for, here's the path! " + mPhotoFile.getAbsolutePath());
        if (savedInstanceState != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_take_picture, container, false);

        mOutputTextView = (TextView) v.findViewById(R.id.take_pic_tv);

        PackageManager packageManager = getActivity().getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCanTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;

        // if cant take photo return error
        if (!mCanTakePhoto) {
            updateUI();
            throw new RuntimeException("Unable to take photo. Must be able to take photo.\n" +
                    "photoFile: " + mPhotoFile + "\n" +
                    "capture image thing: " + captureImage.resolveActivity(packageManager));
        }

        takePicture(captureImage);



        updateUI();
        return v;
    }

    public void updateUI() {
        if (!mCanTakePhoto) {
            mOutputTextView.setText("Can't take a photo! If only we had access to a camera.");
        } else {
            mOutputTextView.setText("Sending you to your favorite camera app!");
        }
    }

    private void takePicture(Intent captureImage) {
        Uri uri = FileProvider.getUriForFile(getActivity(),
                "com.example.sbabb.facial.fileprovider", mPhotoFile);
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        List<ResolveInfo> cameraActivities = getActivity()
                .getPackageManager().queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo activity : cameraActivities) {
            getActivity().grantUriPermission(activity.activityInfo.packageName,
                    uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        startActivityForResult(captureImage, REQUEST_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.example.sbabb.facial.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            sendResult(Activity.RESULT_OK, uri);
        }
    }

    private void sendResult(int resultCode, Uri photoUri) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_PHOTO, photoUri);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable(ARG_PHOTO_NAME, Uri.fromFile(mPhotoFile));
    }

    public static TakePictureFragment newInstance(String photoName) {
        Bundle args = new Bundle();
        TakePictureFragment fragment = new TakePictureFragment();
        args.putString(ARG_PHOTO_NAME, photoName);
        fragment.setArguments(args);
        return fragment;
    }
}
