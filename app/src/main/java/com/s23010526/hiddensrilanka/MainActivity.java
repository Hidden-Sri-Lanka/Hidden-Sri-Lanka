package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    private static final long SPLASH_TIMEOUT = 5000; // 3 seconds Delay between next screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//-----------------------------------------------transition to next page ---------------------------------------------
        // Use a Handler to delay the transition
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an Intent to start the new activity
                Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                startActivity(intent);

                // Finish this activity so the user can't navigate back to the splash screen otherwise when user is pressing bacjk button thaywiil go to splash screen

                finish();
            }
        }, SPLASH_TIMEOUT);

        // -------------------------------------- fading animation ------------------------------------------

        ImageView imageViewToFadeIn = findViewById(R.id.splashImage);

        // Load the fade-in animation
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        // Apply the animation to the ImageView
        imageViewToFadeIn.startAnimation(fadeInAnimation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void toSinUp(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
}