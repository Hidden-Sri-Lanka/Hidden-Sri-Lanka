package com.s23010526.hiddensrilanka;

import android.os.Bundle;


public class FullMapViewActivity extends BaseActivity {

    // TODO: Google Map Implimentations
    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_full_map_view;// map layout file
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.nav_explore_on_map_title); //  string in strings.xml [Explore on Map]
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // TODO: MAP_INTEGRATION - Initialize the map
// Map Integration Point

    }
}