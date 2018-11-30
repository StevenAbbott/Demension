package com.example.sbabb.facial.controllers.addperson;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.sbabb.facial.controllers.base.SingleFragmentActivity;
import com.example.sbabb.facial.controllers.home.HomeFragment;

public class AddPersonActivity extends SingleFragmentActivity {

    public static final String EXTRA_EDIT_PERSON =
            "com.example.sbabb.facial.controllers.addperson.edit_person";

    @Override
    protected Fragment createFragment() {
        return AddPersonFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext, boolean edit) {
        Intent intent = new Intent(packageContext, AddPersonActivity.class);
        intent.putExtra(EXTRA_EDIT_PERSON, edit);
        return intent;
    }
}
