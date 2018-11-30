package com.example.sbabb.facial.controllers.addperson;

import android.os.Bundle;
import android.support.v4.app.Fragment;

// from "add new person button" or "edit person"
// takes in person
// if person is not null, editable fields are populated
// if person is null (navigated to from "add new person but"), displays blank imageView and fields
// when the user exits/ presses save, it will update the image and Person in FB
public class AddPersonFragment extends Fragment{



    public static AddPersonFragment newInstance() {
        Bundle args = new Bundle();
        AddPersonFragment fragment = new AddPersonFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
