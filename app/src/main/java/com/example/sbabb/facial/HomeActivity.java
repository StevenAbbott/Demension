package com.example.sbabb.facial;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class HomeActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);

        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return HomeFragment.newInstance();
    }
}
