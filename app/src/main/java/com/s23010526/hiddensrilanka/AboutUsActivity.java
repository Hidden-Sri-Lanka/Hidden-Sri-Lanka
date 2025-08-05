package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AboutUsActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected String getActivityTitle() {
        return "About Us";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup click listeners for buttons if needed
        setupClickListeners();
    }

    private void setupClickListeners() {
        // You can add click listeners for buttons in the about us page
        // For example, privacy policy, terms of service, contact us buttons
    }

    // These methods can be called from XML onClick attributes
    public void privacyPolicy(View view) {
        // Navigate to privacy policy or show dialog
        // For now, show a coming soon message
        Intent intent = new Intent(this, FetureCommingSoonActivity.class);
        intent.putExtra("FEATURE_NAME", "Privacy Policy");
        startActivity(intent);
    }

    public void Terms(View view) {
        // Navigate to terms of service or show dialog
        Intent intent = new Intent(this, FetureCommingSoonActivity.class);
        intent.putExtra("FEATURE_NAME", "Terms of Service");
        startActivity(intent);
    }

    public void Contact(View view) {
        // Navigate to contact us or show dialog
        Intent intent = new Intent(this, FetureCommingSoonActivity.class);
        intent.putExtra("FEATURE_NAME", "Contact Us");
        startActivity(intent);
    }
}
