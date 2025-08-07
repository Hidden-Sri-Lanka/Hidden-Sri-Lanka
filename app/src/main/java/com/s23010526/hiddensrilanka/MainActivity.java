package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
// Removed: import android.os.Handler; // No longer needed for the primary delay
// Removed: import android.os.Looper;  // No longer needed for the primary delay
// Removed: import android.view.View; // Not directly used in this corrected version
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

// Removed: import androidx.activity.EdgeToEdge; // Not essential for the core logic shown
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    // private static final long SPLASH_TIMEOUT = 5000;  (Changed to another method for delay )
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager
        sessionManager = new SessionManager(this);

        // -------------------------------------- Fading Animation & Transition Logic ------------------------------------------
        ImageView imageViewToFadeIn = findViewById(R.id.splashImage);

        // Load the fade-in animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        // Set an AnimationListener to know when the animation ends
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Check if user is already logged in
                if (sessionManager.isLoggedIn()) {
                    // User is logged in, go directly to HomeActivity
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("USERNAME", sessionManager.getUsername());
                    startActivity(intent);
                } else {
                    // User is not logged in, go to WelcomeActivity
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }

                // Finish this MainActivity so the user can't navigate back to the splash screen
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated
            }
        });

        // Apply the animation to the ImageView
        imageViewToFadeIn.startAnimation(fadeInAnimation);

        // --- Original Handler that i used for  delayed transition no longer need ---
        /*
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);
        */

        // --------------------------------------------finshed------------------------------------

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}