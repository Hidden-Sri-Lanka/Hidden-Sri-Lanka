package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.s23010526.hiddensrilanka.databinding.ActivityBaseBinding;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BaseActivity";
    protected ActivityBaseBinding binding; // Binding for the base layout
    private ActionBarDrawerToggle drawerToggle;

    @LayoutRes
    protected abstract int getLayoutResourceId(); // Child activities need to e provide their content layout

    protected abstract String getActivityTitle(); // Child activities needto  provide their titles

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBaseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(getLayoutResourceId(), binding.contentFrame, true);

        // Setup Toolbar with custom layout
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            // Disable the default title since we're using custom TextView
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Set custom title text
        TextView toolbarTitle = binding.toolbar.findViewById(R.id.toolbar_title);
        if (toolbarTitle != null) {
            toolbarTitle.setText(getActivityTitle());
        }

        // Setup search field functionality
        EditText searchField = binding.toolbar.findViewById(R.id.toolbar_search_field);
        ImageView searchIcon = binding.toolbar.findViewById(R.id.search_icon);

        if (searchField != null && searchIcon != null) {
            // Handle search input
            searchField.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                    String query = searchField.getText().toString().trim();
                    if (!query.isEmpty()) {
                        performSearch(query);
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

        // Setup Navigation Drawer
        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);
        binding.navView.bringToFront();

        // Handle WindowInsets for Edge to Edge display - FIXED
        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            // Remove the problematic toolbar padding that was causing misalignment
            return WindowInsetsCompat.CONSUMED;
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Intent intent = null;

        Class<?> currentActivityClass = this.getClass();

        if (itemId == R.id.nav_home) {
            if (currentActivityClass != HomeActivity.class) {
                intent = new Intent(this, HomeActivity.class);
            }
            Toast.makeText(this, "Home Clicked", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_settings) {
            if (currentActivityClass != SettingsActivity.class) {
                intent = new Intent(this, SettingsActivity.class);
            }
            Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_exp_map) {
            if (currentActivityClass != FullMapViewActivity.class) {
                intent = new Intent(this, FullMapViewActivity.class);
            }
            Toast.makeText(this, "Map Page Clicked", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_favorit) {
            Toast.makeText(this, "Favorites Feature Coming Soon...", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_about_us) {
            if (currentActivityClass != AboutUsActivity.class) {
                intent = new Intent(this, AboutUsActivity.class);
            }
            Toast.makeText(this, "About Us Clicked", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_profile) {
            Toast.makeText(this, "Profile Feature not available Now ...", Toast.LENGTH_SHORT).show();
        } else if (itemId == R.id.nav_log_out) {
            intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show();
        }

        if (intent != null) {
            startActivity(intent);
            if (itemId == R.id.nav_log_out) {
                finish(); // Finishing  current activity after logging out
            } else if (currentActivityClass != intent.getComponent().getClass()) {

            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Removed old search menu since we now have integrated search field in toolbar
        // No need to inflate any menu items as search is now integrated
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) { // Let drawer toggle handle hamburger icon
            return true;
        }
        // Removed old search menu item handling since we have integrated search field
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void performSearch(String query) {
        // Implement search functionality in child classes or here
        Toast.makeText(this, "Search performed for query: " + query, Toast.LENGTH_SHORT).show();
    }
}