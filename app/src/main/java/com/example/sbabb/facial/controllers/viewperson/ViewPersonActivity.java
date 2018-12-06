package com.example.sbabb.facial.controllers.viewperson;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.example.sbabb.facial.controllers.addperson.AddPersonActivity;
import com.example.sbabb.facial.controllers.base.SingleFragmentActivity;

public class ViewPersonActivity extends SingleFragmentActivity {

    private static final String EXTRA_PERSON = "extrra_personn";

    @Override
    protected Fragment createFragment() {
        return new ViewPersonFragment();
    }

    public static Intent newIntent(Context packageContext, String personKey) {
        Intent intent = new Intent(packageContext, AddPersonActivity.class);
        intent.putExtra(EXTRA_PERSON, personKey);
        return intent;
    }
}
