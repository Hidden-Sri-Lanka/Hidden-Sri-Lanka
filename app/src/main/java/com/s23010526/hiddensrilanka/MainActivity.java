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

    // private static final long SPLASH_TIMEOUT = 5000; // This is no longer the primary delay mechanism

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // -------------------------------------- Fading Animation & Transition Logic ------------------------------------------
        ImageView imageViewToFadeIn = findViewById(R.id.splashImage);

        // Load the fade-in animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        // Set an AnimationListener to know when the animation ends
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Animation started (you can add log statements or other actions here if needed)
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Animation ended, NOW start the transition to WelcomeActivity
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);

                // Finish this MainActivity so the user can't navigate back to the splash screen
                finish();

                // If you wanted an *additional* short delay *after* the animation
                // but *before* transitioning, you could add a new Handler here.
                // For most splash screens, transitioning immediately after animation is fine.
                /*
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 500); // e.g., an additional 0.5-second delay
                */
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Animation repeated (not typical for a simple fade-in)
            }
        });

        // Apply the animation to the ImageView
        imageViewToFadeIn.startAnimation(fadeInAnimation);

        // --- Original Handler for delayed transition is now REMOVED or COMMENTED OUT ---
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

        // -------------------------------------- Window Insets Handling ------------------------------------------
        // This part handles fitting the layout to screen edges, you can keep it as is.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}