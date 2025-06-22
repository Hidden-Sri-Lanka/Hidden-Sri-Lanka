package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class AboutUsActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.title_about_us);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // --- Methods for button clicks, linkes ---

    public void privacyPolicy(View view) { // for android:onClick="privacyPolicy"
        navigateToFeatureComingSoon(getString(R.string.feature_privacy_policy));
    }

    public void Terms(View view) { // for android:onClick="Terms"
        navigateToFeatureComingSoon(getString(R.string.feature_terms_conditions));
    }

    public void Contact(View view) { // for android:onClick="Contact"
        navigateToFeatureComingSoon(getString(R.string.feature_contact_us));
    }

    private void navigateToFeatureComingSoon(String featureName) {
        Intent intent = new Intent(this, FetureCommingSoonActivity.class);
        Toast.makeText(this, featureName + " " + getString(R.string.coming_soon_suffix), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }
}