package com.s23010526.hiddensrilanka;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
// Import the correct Toolbar from androidx.appcompat
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge is not needed when manually setting padding for system bars.
        // You can comment it out or remove it if it causes issues with the toolbar color.
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Hooks for Nav view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Use this toolbar as the action bar
        setSupportActionBar(toolbar);

        // --- CHANGE 1: SET THE TITLE ---
        // Add this line to set the title to "Settings"
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
        }

        // Nav drawer menu toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // This listener handles insets (like the status bar) but can interfere with simple layouts.
        // If your toolbar color doesn't extend to the top of the screen, you might need to adjust this.
        // For a simple white toolbar, this should be okay.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom); // Set top padding to 0
            toolbar.setPadding(0, systemBars.top, 0, 0); // Apply top padding to the toolbar instead
            return insets;
        });
    }

    // --- CHANGE 2: INFLATE THE MENU TO SHOW THE SEARCH ICON ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // Optional: Add listeners for search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // User pressed search button
                Toast.makeText(SettingsActivity.this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Text in search field changed
                return false;
            }
        });

        return true;
    }

    // --- CHANGE 3: HANDLE CLICKS ON MENU ITEMS (LIKE SEARCH) ---
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            // This can be used if you want to perform an action when the icon itself is clicked,
            // but the main logic is often in the SearchView listener above.
            Toast.makeText(this, "Search icon clicked!", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- Bonus Tip: Handle Navigation Drawer Clicks ---
    // You'll also want to handle clicks on your navigation items.
    // Set a listener in onCreate:
    // navigationView.setNavigationItemSelectedListener(this);
    // Then implement the listener method.
}