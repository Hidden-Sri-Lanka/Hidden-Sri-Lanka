package com.s23010526.hiddensrilanka;

import android.os.Bundle;

public class FetureCommingSoonActivity extends BaseActivity { // Changed to BaseActivity


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_feture_comming_soon; //layout file
    }

    @Override
    protected String getActivityTitle() {
        String featureName = getIntent().getStringExtra("FEATURE_NAME");
        if (featureName != null && !featureName.isEmpty()) {
            return featureName; // Set the toolbar title to the specific feature
        }
        return "Coming Soon"; // defoult title
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}