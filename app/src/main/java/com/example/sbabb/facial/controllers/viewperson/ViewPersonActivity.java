package com.example.sbabb.facial.controllers.viewperson;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.example.sbabb.facial.controllers.addperson.AddPersonActivity;
import com.example.sbabb.facial.controllers.base.SingleFragmentActivity;

public class ViewPersonActivity extends SingleFragmentActivity {

    private static final String EXTRA_PHOTO = "extrra_photot";

    @Override
    protected Fragment createFragment() {
        return new ViewPersonFragment();
    }

    public static Intent newIntent(Context packageContext, Uri photoUri) {
        Intent intent = new Intent(packageContext, AddPersonActivity.class);
        intent.putExtra(EXTRA_PHOTO, photoUri);
        return intent;
    }
}
