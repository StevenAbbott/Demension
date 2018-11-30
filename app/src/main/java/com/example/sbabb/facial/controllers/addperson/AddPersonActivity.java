package com.example.sbabb.facial.controllers.addperson;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.sbabb.facial.controllers.base.SingleFragmentActivity;

public class AddPersonActivity extends SingleFragmentActivity {

    public static final String EXTRA_PERSON_KEY =
            "com.example.sbabb.facial.controllers.addperson.person";

    @Override
    protected Fragment createFragment() {
        String personKey = getIntent().getStringExtra(EXTRA_PERSON_KEY);
        return AddPersonFragment.newInstance(personKey);
    }

    public static Intent newIntent(Context packageContext, boolean edit, String personKey) {
        Intent intent = new Intent(packageContext, AddPersonActivity.class);
        intent.putExtra(EXTRA_PERSON_KEY, personKey);
        return intent;
    }
}
