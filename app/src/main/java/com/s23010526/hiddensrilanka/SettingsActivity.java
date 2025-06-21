package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Keep if you have onClick methods in XML for settings content
import android.widget.Toast; // Keep for specific Toasts if needed


public class SettingsActivity extends BaseActivity { // Changed to BaseActivity
    // TODO: SETTINGS_CONTENT_SETUP - Declare UI elements for your settings screen content here
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_settings; // This XML only for the settings screen
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.nav_settings_title); //title - Settings
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // CRUCIAL call to BaseActivity's onCreate

        // TODO: Settings page logic and features
    }
}
