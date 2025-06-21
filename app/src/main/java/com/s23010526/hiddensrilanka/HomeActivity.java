package com.s23010526.hiddensrilanka;

import android.os.Bundle;

public class HomeActivity extends BaseActivity { // Changed to BaseActivity

    // TODO: HOME_CONTENT_SETUP - Declare UI elements for your home screen content here
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home; // This XML file is oly for the home screen
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.nav_home_title); //title is "Home"
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- TODO: Home Screen Logic &  Features ---

    }
}