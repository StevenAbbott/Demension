package com.example.sbabb.facial;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class HomeFragment extends Fragment {





    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
