package com.s23010526.hiddensrilanka;

import android.os.Bundle;
// Import the correct Toolbar from androidx.appcompat
import androidx.appcompat.widget.Toolbar; // <--- CHANGE THIS

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends AppCompatActivity {

    //Variables for Nav Viwe
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar; // This will now be androidx.appcompat.widget.Toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

//        Hooks for Nav viwe
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        // drawerLayout = findViewById(R.id.drawer_layout); // This line is redundant, remove it


//        Saying use this toolbar as the nav Tool Bar
        // Use setSupportActionBar for androidx.appcompat.widget.Toolbar
        setSupportActionBar(toolbar); // <--- CHANGE THIS


//        nav drawer menu
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Assuming R.id.drawer_layout is your root view for EdgeToEdge insets,
        // if R.id.main was a specific content container.
        // If your DrawerLayout has android:id="@+id/drawer_layout", use that.
        // If the root is something else, adjust accordingly.
        // In the previous XML example, the root DrawerLayout had android:id="@+id/drawer_layout"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}