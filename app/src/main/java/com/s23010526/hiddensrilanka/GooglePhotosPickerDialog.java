package com.s23010526.hiddensrilanka;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Simple dialog for adding Google Photos URLs
 * This replaces complex API integration with user-friendly URL input
 * Perfect for explaining in viva - clear, simple, effective
 */
public class GooglePhotosPickerDialog {

    public interface OnPhotoSelectedListener {
        void onPhotoSelected(String imageUrl);
    }

    /**
     * Shows a simple dialog for users to paste Google Photos share links
     * Provides clear instructions for users
     */
    public static void show(Context context, OnPhotoSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Image from Google Photos");

        // Clear instructions for users
        String instructions = "📱 How to get Google Photos link:\n\n" +
                "1. Open Google Photos app\n" +
                "2. Select your photo\n" +
                "3. Tap Share button\n" +
                "4. Choose 'Copy link'\n" +
                "5. Paste the link below";

        builder.setMessage(instructions);

        // Create input field with styling
        EditText urlInput = new EditText(context);
        urlInput.setHint("Paste your Google Photos link here...");
        urlInput.setPadding(50, 30, 50, 30);
        urlInput.setSingleLine(false);
        urlInput.setMaxLines(3);

        builder.setView(urlInput);

        // Add buttons
        builder.setPositiveButton("Add Photo", (dialog, which) -> {
            String url = urlInput.getText().toString().trim();

            if (isValidGooglePhotosUrl(url)) {
                // Process the URL and return it
                String processedUrl = processGooglePhotosUrl(url);
                listener.onPhotoSelected(processedUrl);
                Toast.makeText(context, "Photo added successfully! 📸", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Please enter a valid Google Photos link", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", null);

        // Show help button
        builder.setNeutralButton("Help", (dialog, which) -> {
            showHelpDialog(context);
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Validates Google Photos URL
     * Simple validation that's easy to explain
     */
    private static boolean isValidGooglePhotosUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        // Check for Google Photos domains
        return url.contains("photos.google.com") ||
               url.contains("photos.app.goo.gl") ||
               url.contains("lh3.googleusercontent.com");
    }

    /**
     * Processes Google Photos URL to make it suitable for loading
     * Adds parameters for optimal image loading
     */
    private static String processGooglePhotosUrl(String url) {
        // If it's already a direct Google Photos URL, optimize it
        if (url.contains("photos.google.com") && !url.contains("=")) {
            return url + "=w800-h600-c"; // width=800, height=600, crop
        }

        // Return as-is if already processed or different format
        return url;
    }

    /**
     * Shows help dialog with detailed instructions
     */
    private static void showHelpDialog(Context context) {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(context);
        helpBuilder.setTitle("📱 Google Photos Help");

        String helpText = "Why use Google Photos links?\n\n" +
                "✅ Free - No storage costs\n" +
                "✅ Fast - Images load quickly\n" +
                "✅ Reliable - Google's servers\n" +
                "✅ Easy - Just copy & paste\n\n" +
                "Troubleshooting:\n" +
                "• Make sure the photo is public/shared\n" +
                "• Check your internet connection\n" +
                "• Try copying the link again";

        helpBuilder.setMessage(helpText);
        helpBuilder.setPositiveButton("Got it!", null);
        helpBuilder.show();
    }
}
