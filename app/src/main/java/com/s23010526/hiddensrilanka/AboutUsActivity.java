package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast; //  show Toasts msges

import androidx.activity.EdgeToEdge;

public class AboutUsActivity extends BaseActivity {


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_about_us;
        // NOT the DrawerLayout, Toolbar, etc.
    }

    // Adding the title for the Toolbar
    @Override
    protected String getActivityTitle() {
        return getString(R.string.title_about_us);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // setting up BaseActivity.


    }
    // --- These methods are tempory for AboutUs Page when i create the all screens thay will added  content ---
//Template for future me
//    public void toSettings(View view) {
//        Intent intent = new Intent(this, AboutUsActivity.class);
//        startActivity(intent);

    public void privacyPolicy(View view) { // for android:onClick="privacyPolicy"
        navigateToFeatureComingSoon(getString(R.string.feature_privacy_policy));
    }

    public void Terms(View view) { // for terms and conditions
        navigateToFeatureComingSoon(getString(R.string.feature_terms_conditions));
    }

    public void Contact(View view) { // for contact us
        navigateToFeatureComingSoon(getString(R.string.feature_contact_us));
    }

    private void navigateToFeatureComingSoon(String featureName) {
        Intent intent = new Intent(this, FetureCommingSoonActivity.class); // this will give us smal toas massage
        Toast.makeText(this, featureName + " " + getString(R.string.coming_soon_suffix), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}