package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
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

        // Setup Toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getActivityTitle());
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

        // Handle WindowInsets for Edge to Edge display
        ViewCompat.setOnApplyWindowInsetsListener(binding.drawerLayout, (v, windowInsets) -> {
            Insets systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            binding.toolbar.setPadding(0, systemBars.top, 0, 0);
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
        getMenuInflater().inflate(R.menu.settings_toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQueryHint(getString(R.string.search_hint));
                searchView.setMaxWidth(Integer.MAX_VALUE);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Toast.makeText(BaseActivity.this,
                                getString(R.string.searching_for_query, query),
                                Toast.LENGTH_SHORT).show();
                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });
            } else {
            }
        } else {
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) { // Let drawer toggle handle hamburger icon
            return true;
        }
        if (item.getItemId() == R.id.action_search) {
            Toast.makeText(this, getString(R.string.search_icon_clicked), Toast.LENGTH_SHORT).show();
            return true;
        }
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
}