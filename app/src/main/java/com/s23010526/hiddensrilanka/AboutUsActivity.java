package com.s23010526.hiddensrilanka;
// package declearation this is where i say the where classes is belong

//intent is used to navigate between defferent pages
import android.content.Intent;
//bundle is use to pass data between activities/screens
import android.os.Bundle;
//View represent buttons or UI elements
import android.view.View;
import android.net.Uri;
import android.widget.Toast;


// About us activity is inherrits common feturs  from Base activity
//i have created basic template called BaseActivity.java
// this has reduse code repeting
public class AboutUsActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_about_us;
    } // this telles witch xml tile to use in this case it is "activity_about_us.xml"

//    about "getActivityTitle" Mothod
        // this method sets title of the page (top of the page - toolbar/top bar)
    @Override
    protected String getActivityTitle() {

        return "About Us"; // sets as About Us
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // this is where activity first create (birth of the screen)

        //
        setupClickListeners();
    }

    private void setupClickListeners() {
//        currently these handle by xml code
//        findViewById(R.id.btnPrivacy).setOnClickListener(v -> privacyPolicy(v));
//        findViewById(R.id.btnTerms).setOnClickListener(v -> Terms(v));
//        findViewById(R.id.btnContact).setOnClickListener(v -> Contact(v));
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
