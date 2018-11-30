package com.example.sbabb.facial.controllers.viewperson;

import android.os.Bundle;
import android.support.v4.app.Fragment;

// from "take picture button" or library
// takes in bitmap of person's face
// if bitmap is null (navigated to by "take pic but), sends intent to camera to get picture
// uses faceID to find whose face it is
// displays their info in a viewing window
public class ViewPersonFragment extends Fragment {



    public static ViewPersonFragment newInstance() {
        Bundle args = new Bundle();
        ViewPersonFragment fragment = new ViewPersonFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
