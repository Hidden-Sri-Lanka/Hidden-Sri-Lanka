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
//        currently these handle by xml code (Un nessosry) TODO : Cleanup
//        findViewById(R.id.btnPrivacy).setOnClickListener(v -> privacyPolicy(v));
//        findViewById(R.id.btnTerms).setOnClickListener(v -> Terms(v));
//        findViewById(R.id.btnContact).setOnClickListener(v -> Contact(v));
    }

    // These methods can be called from XML onClick attributes
    public void privacyPolicy(View view) {
        // Navigate to privacy policy or show dialog
        // For now, show a coming soon message
//        Intent intent = new Intent(this, FetureCommingSoonActivity.class);
//        intent.putExtra("FEATURE_NAME", "Privacy Policy");
//        startActivity(intent);
        openUrl("https://github.com/Hidden-Sri-Lanka/Hidden-Sri-Lanka/blob/main/docs/PrivacyPolicy/PrivacyPolicy.md");

    }

    public void Terms(View view) {
        // Navigate to terms of service or show dialog
//        Intent intent = new Intent(this, FetureCommingSoonActivity.class);
//        intent.putExtra("FEATURE_NAME", "Terms of Service");
//        startActivity(intent);
        openUrl("https://github.com/Hidden-Sri-Lanka/Hidden-Sri-Lanka/blob/main/docs/PrivacyPolicy/TermsAndConditions.md");
    }

    public void Contact(View view) {
        // Navigate to contact us or show dialog
//        Intent intent = new Intent(this, FetureCommingSoonActivity.class);
//        intent.putExtra("FEATURE_NAME", "Contact Us");
//        startActivity(intent);
        openUrl("http://asitha.site/"); //button click handler
    }
    // Reusable Helper method to open URLs
    private void openUrl(String url) { // get url as a string
        try {
            Uri uri = Uri.parse(url); // this will convert humen readable url to android readable url address (can check this in .net for android in ms )
            Intent intent = new Intent(Intent.ACTION_VIEW, uri); // this tells that i need to view this content (this is system wide action)
            // explain
            // when url -> web browser , phone numbers  -> dial pad, map-> open maps app
//
            // following part is optional it check is ther any app that can open this url
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else { // if no app can handle this request then follownig error will show
                Toast.makeText(this, "No application can handle this request. Please install a web browser.",
                        Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this,
                    "Error while opening URL: " + e.getMessage(), // if url opening is giving error
                    Toast.LENGTH_LONG).show();
        }
    }
}
// Toast massages are tempory popup massages these are deffernt comaire to error msges
