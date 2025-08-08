package com.s23010526.hiddensrilanka;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Simple helper class for Google Photos URL integration
 * This class helps users add Google Photos shareable links as image sources
 * Perfect for Firebase free tier - no storage costs, just URL references
 */
public class GooglePhotosUrlHelper {

    /**
     * Shows a dialog to input Google Photos share URL
     * Validates the URL and loads the image preview
     */
    public static void showGooglePhotosUrlDialog(Context context, ImageUrlCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Google Photos Image");
        builder.setMessage("How to add images:\n\n" +
                "Method 1 - Google Photos Share Link:\n" +
                "1. Open Google Photos app/website\n" +
                "2. Select your image\n" +
                "3. Tap Share → Copy link\n" +
                "4. Paste the link below\n\n" +
                "Method 2 - Direct Image URL:\n" +
                "Use any direct image URL from the web\n\n" +
                "Both types of links are supported!");

        // Create input field
        EditText urlInput = new EditText(context);
        urlInput.setHint("Paste Google Photos link or direct image URL...");
        urlInput.setPadding(50, 30, 50, 30);
        builder.setView(urlInput);

        // Add buttons
        builder.setPositiveButton("Add Image", (dialog, which) -> {
            String url = urlInput.getText().toString().trim();
            if (validateAnyImageUrl(url)) {
                processImageUrl(context, url, callback);
            } else {
                // Just call callback with null instead of showing toast
                callback.onImageUrlProcessed(null);
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * Public method to process image URL - converts Google Photos share links if needed
     * This method handles both Google Photos links and direct image URLs
     */
    public static void processImageUrl(Context context, String imageUrl, ImageUrlCallback callback) {
        // Validate input
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            android.util.Log.w("GooglePhotosHelper", "Empty or null image URL provided");
            callback.onImageUrlProcessed(null);
            return;
        }

        final String finalImageUrl = imageUrl.trim(); // Create final copy for lambda

        // Check if it's a Google Photos URL that needs processing
        if (!isGooglePhotosUrl(finalImageUrl)) {
            // Direct image URL - return as is
            callback.onImageUrlProcessed(finalImageUrl);
            return;
        }

        // Process Google Photos URL in background thread
        new Thread(() -> {
            try {
                String processedUrl = extractDirectImageUrl(finalImageUrl);

                // Update UI on main thread
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onImageUrlProcessed(processedUrl);
                });

            } catch (Exception e) {
                android.util.Log.e("GooglePhotosHelper", "Error processing URL: " + e.getMessage());

                // Fall back to original URL on main thread
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onImageUrlProcessed(finalImageUrl);
                });
            }
        }).start();
    }

    /**
     * Interface for URL processing callback
     */
    public interface ImageUrlCallback {
        void onImageUrlProcessed(String imageUrl);

        // For backward compatibility with existing code
        default void onUrlSelected(String imageUrl) {
            onImageUrlProcessed(imageUrl);
        }
    }

    /**
     * Check if URL is a Google Photos URL that needs processing
     */
    public static boolean isGooglePhotosUrl(String url) {
        return url != null && (
                url.contains("photos.app.goo.gl") ||
                url.contains("photos.google.com/share") ||
                url.contains("photos.google.com/u/") ||
                url.contains("photos.google.com/album")
        );
    }

    /**
     * Extract direct image URL from Google Photos share link
     */
    private static String extractDirectImageUrl(String shareUrl) {
        try {
            // Method 1: Try to follow redirects
            String finalUrl = followRedirects(shareUrl);
            if (finalUrl != null && finalUrl.contains("googleusercontent.com")) {
                return finalUrl;
            }

            // Method 2: Try HTML extraction
            String htmlUrl = extractImageFromHtml(shareUrl);
            if (htmlUrl != null && !htmlUrl.isEmpty()) {
                return htmlUrl;
            }

            // Fallback: return original URL
            return shareUrl;

        } catch (Exception e) {
            android.util.Log.e("GooglePhotosHelper", "Error extracting URL: " + e.getMessage());
            return shareUrl;
        }
    }

    /**
     * Checks if URL is a Google Photos share link
     */
    private static boolean isGooglePhotosShareLink(String url) {
        return url != null && (
            url.contains("photos.app.goo.gl") ||
            url.contains("photos.google.com/share") ||
            url.contains("photos.google.com/u/") ||
            url.contains("photos.google.com/album")
        );
    }

    /**
     * Validates if the URL could be any type of image URL
     */
    private static boolean validateAnyImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        String lowerUrl = url.toLowerCase();
        
        // Accept Google Photos links
        if (isGooglePhotosShareLink(url)) {
            return true;
        }

        // Accept direct image URLs
        if (lowerUrl.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp)(\\?.*)?$")) {
            return true;
        }
        
        // Accept Google user content URLs
        if (lowerUrl.contains("googleusercontent.com")) {
            return true;
        }
        
        // Accept common image hosting services
        if (lowerUrl.contains("imgur.com") ||
            lowerUrl.contains("flickr.com") ||
            lowerUrl.contains("unsplash.com") ||
            lowerUrl.contains("pexels.com") ||
            lowerUrl.contains("cloudinary.com") ||
            lowerUrl.contains("amazonaws.com")) {
            return true;
        }

        // For other URLs, basic validation
        return lowerUrl.startsWith("http") && !lowerUrl.contains("javascript:");
    }

    /**
     * AsyncTask to extract direct image URL from Google Photos share link
     */
    private static class ExtractImageUrlTask extends AsyncTask<String, Void, String> {
        private Context context;
        private ImageUrlCallback callback;
        private String originalUrl; // Store original URL for fallback

        public ExtractImageUrlTask(Context context, ImageUrlCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                String shareUrl = urls[0];
                originalUrl = shareUrl; // Store for potential use in onPostExecute

                // For Google Photos share links, the best approach is often to use the original URL
                // as many image loading libraries can handle the redirects properly

                // Method 1: Try to follow redirects to get the actual URL
                String finalUrl = followRedirects(shareUrl);
                if (finalUrl != null && !finalUrl.equals(shareUrl)) {
                    // If we got a different URL, it might be the direct image URL
                    if (finalUrl.contains("googleusercontent.com") || finalUrl.contains("ggpht.com")) {
                        return finalUrl;
                    }
                }

                // Method 2: Try to extract from HTML content
                String htmlExtractedUrl = extractImageFromHtml(shareUrl);
                if (htmlExtractedUrl != null && !htmlExtractedUrl.isEmpty()) {
                    return htmlExtractedUrl;
                }

                // Method 3: Fallback - return original URL as many modern image loaders can handle redirects
                // Google Photos share links often work directly in image loading libraries
                return shareUrl;

            } catch (Exception e) {
                e.printStackTrace();
                // Return original URL as fallback
                return originalUrl != null ? originalUrl : urls[0];
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.isEmpty()) {
                callback.onUrlSelected(result);
            } else {
                // Fallback - use original URL
                callback.onUrlSelected(originalUrl);
            }
        }
    }

    /**
     * Extract image ID from Google Photos URL
     */
    private static String extractImageIdFromUrl(String url) {
        try {
            // Pattern for Google Photos share URLs
            Pattern pattern = Pattern.compile("photos\\.app\\.goo\\.gl/([a-zA-Z0-9]+)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }

            // Try other patterns
            pattern = Pattern.compile("/photo/([a-zA-Z0-9_-]+)");
            matcher = pattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Follow redirects to get final URL
     */
    private static String followRedirects(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.connect();

            return connection.getURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Try to extract image URL from HTML content
     */
    private static String extractImageFromHtml(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder html = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                html.append(line);

                // Look for image URLs in the HTML
                if (line.contains("googleusercontent.com")) {
                    Pattern pattern = Pattern.compile("https://lh[0-9]\\.googleusercontent\\.com/[^\"'\\s]+");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        return matcher.group(0);
                    }
                }
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load image from URL using Glide with error handling
     */
    public static void loadImageFromUrl(Context context, String url, ImageView imageView) {
        Glide.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView);
    }
}
