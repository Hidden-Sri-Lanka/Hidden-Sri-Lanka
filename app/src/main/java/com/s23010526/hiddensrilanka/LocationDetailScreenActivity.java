package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;

public class LocationDetailScreenActivity extends BaseActivity { // Changed to BaseActivity
//TODO : Still in Inplimentation Part
    // TODO: DETAIL_CONTENT_SETUP - Declare UI elements for the location detail screen

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_location_detail_screen;
    }

    @Override
    protected String getActivityTitle() {
        String locationName = getIntent().getStringExtra("LOCATION_NAME_EXTRA");
        return locationName != null ? locationName : "Location Details"; // Default title
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- TODO: Future Feature Integration  here  ---


    }
}