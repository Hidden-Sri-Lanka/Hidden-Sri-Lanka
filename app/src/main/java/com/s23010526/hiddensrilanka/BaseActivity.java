package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.activity.OnBackPressedCallback;

import com.google.android.material.navigation.NavigationView;
import com.s23010526.hiddensrilanka.databinding.ActivityBaseBinding;


// This class is the class that provide all ther shered fetures for all activities (cmmon fetures )
public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected ActivityBaseBinding binding; // Binding for the base layout
    private ActionBarDrawerToggle drawerToggle;
    protected SessionManager sessionManager; // helps to login and logout

    @LayoutRes
    protected abstract int getLayoutResourceId(); // Child activities need to provide their content layout (xml file )

    protected abstract String getActivityTitle(); // need to provide their titles (Home ,about,etc...)

    @Override
    protected void onCreate(Bundle savedInstanceState) { // calling when activity starting
        super.onCreate(savedInstanceState);

        // Initialize SessionManager (login helper)
        sessionManager = new SessionManager(this);

        binding = ActivityBaseBinding.inflate(getLayoutInflater()); //loading xml file
        setContentView(binding.getRoot());

        LayoutInflater inflater = LayoutInflater.from(this); // this will load xml uniqe iterms in to base template frame
        inflater.inflate(getLayoutResourceId(), binding.contentFrame, true);

        // Setup Toolbar with custom layout
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) { // hides default title
            // Disable the default title since we're using custom TextView
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set custom title text
        TextView toolbarTitle = binding.toolbar.findViewById(R.id.toolbar_title);
        if (toolbarTitle != null) {
            toolbarTitle.setText(getActivityTitle()); // if title is not provbided it will automatically assign activity name  as title
        }

        // add serch box and icon to the toolbar
        EditText searchField = binding.toolbar.findViewById(R.id.toolbar_search_field);
        ImageView searchIcon = binding.toolbar.findViewById(R.id.search_icon);

        if (searchField != null && searchIcon != null) {
            // Handle search input
            searchField.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    String query = searchField.getText().toString().trim();
                    if (!query.isEmpty()) {
                        performSearch(query); // if tutch on serch bar it will call performSearch(quary) method
                        searchField.clearFocus();
                        // Hide keyboard
                        android.view.inputmethod.InputMethodManager imm =
                                (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            });

            // Handle search icon click
            searchIcon.setOnClickListener(v -> {
                String query = searchField.getText().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                    searchField.clearFocus();
                }
            });
        }

        // Setup Navigation Drawer toggle (hamburger menue icon)
        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState(); // setting icon animation correctly

        binding.navView.setNavigationItemSelectedListener(this);
        binding.navView.bringToFront();

        // Handle back button press using OnBackPressedCallback to manage drawer state
        // if drower is open then close it
        // othervise perform normal actions
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // Handle WindowInsets for Edge to Edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    // menue iterms clicks handling
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;

        Class<?> currentActivityClass = this.getClass();

        if (itemId == R.id.nav_home) {
            intent = new Intent(this, MainActivity.class);// home page
        } else if (itemId == R.id.nav_settings) {
            if (currentActivityClass != SettingsActivity.class) {
                intent = new Intent(this, SettingsActivity.class);// settings page
            }
        } else if (itemId == R.id.nav_exp_map) {
            if (currentActivityClass != FullMapViewActivity.class) {// map
                intent = new Intent(this, FullMapViewActivity.class);
            }
        } else if (itemId == R.id.nav_add_location) {
            if (currentActivityClass != AddLocationActivity.class) {
                intent = new Intent(this, AddLocationActivity.class);// add location page
            }
        } else if (itemId == R.id.nav_favorit) {

            intent = new Intent(this, FetureCommingSoonActivity.class); // favorits page (under development )TODO : fev page dev
        } else if (itemId == R.id.nav_about_us) {
            if (currentActivityClass != AboutUsActivity.class) {
                intent = new Intent(this, AboutUsActivity.class);// about us
            }
        } else if (itemId == R.id.nav_profile) {
            intent = new Intent(this, FetureCommingSoonActivity.class); // TODO : profiles
        } else if (itemId == R.id.nav_log_out) {
            // Clear session data
            if (sessionManager != null) {
                sessionManager.logoutUser(); // session manager will clear all the saved data
            }

            // Redirect to WelcomeActivity and clear the activity stack
            intent = new Intent(this, WelcomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            return false;
        }

        if (intent != null) {
            startActivity(intent);
            if (itemId == R.id.nav_log_out) {
                finish(); // Finish current activity after logging out
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // No need to inflate any menu items as search is now integrated to toolbar
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) { // Let drawer toggle handle hamburger icon
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void performSearch(String searchQuery) {
        // Override in child classes to implement search functionality
        // Base implementation can handle common search logic if needed
    }
}