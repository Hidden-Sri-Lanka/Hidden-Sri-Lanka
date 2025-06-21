package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log for debugging
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
<<<<<<< HEAD
import android.widget.Toast; //  show Toasts msges

import androidx.activity.EdgeToEdge;
=======
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar; // Ensure you import the correct Toolbar
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout; // Import DrawerLayout
import com.google.android.material.navigation.NavigationView; // Import NavigationView
>>>>>>> 08964b1632d81070c9168405a6e9934cb9195cc6

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

    // variables for drawer and toolbar
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
<<<<<<< HEAD
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
=======
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about_us);

        //<------------------------------------------------ Navigation top Tool Bar------------------------------------------------------------->
        // Hooks for Nav view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
// checking null points if null point is  visible then quiting the activity
        if (drawerLayout == null) {
            Log.e("AboutUsActivity", "onCreate :- drawerLayout not found!");
            // exception or handleing the error
            return;
        }
        //if toolbar is not foud then brake this prosess
        if (toolbar == null) {
            Log.e("AboutUsActivity", "onCreate :- toolbar not found!");
            return;
        }
        if (navigationView == null) {
            Log.e("AboutUsActivity", "onCreate :- navigationView not found!");
        }


        // Use this toolbar as the action bar
        setSupportActionBar(toolbar);

        // Add this line to set the title to "About Us"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("About Us"); //title name change to About us
        }

        // Nav drawer menu toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        ViewCompat.setOnApplyWindowInsetsListener(drawerLayout, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            toolbar.setPadding(0, systemBars.top, 0, 0);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    // ------------------------------ Adding Search icon and feature--------------------------------- ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQueryHint("Search...");
                searchView.setMaxWidth(Integer.MAX_VALUE);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        // User pressed search button
                        Toast.makeText(AboutUsActivity.this, "Searching for: " + query, Toast.LENGTH_SHORT).show(); // Corrected context
                        return false; //press the handle its true
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
// text feald change
                        return false;
                    }
                });
            } else {
                Log.e("AboutUsActivity", "onCreateOptionsMenu:- SearchView is null");
            }
        } else {
            Log.e("AboutUsActivity", "onCreateOptionsMenu:- action_search MenuItem not found");
        }
        return true;
    }

    // --- handle clicks on menu icon ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_search) {
            Toast.makeText(this, "Search icon clicked!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


//    Methods for buttons like tos,contact,and other
    public void privacyPolicy(View view) {
        Intent intent = new Intent(this, FetureCommingSoonActivity.class);
        startActivity(intent);
>>>>>>> 08964b1632d81070c9168405a6e9934cb9195cc6
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