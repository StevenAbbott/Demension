package com.example.sbabb.facial.controllers.viewperson;

import android.support.v4.app.Fragment;

import com.example.sbabb.facial.controllers.base.SingleFragmentActivity;

public class ViewPersonActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ViewPersonFragment();
    }
}
