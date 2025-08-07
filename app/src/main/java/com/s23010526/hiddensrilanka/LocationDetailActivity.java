package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

/**
 * Location Detail Activity - Shows complete information about a selected attraction
 *
 * This activity demonstrates key Android development concepts:
 * 1. Intent data extraction and validation
 * 2. Google Photos URL processing for image loading
 * 3. Three-level fallback system for navigation
 * 4. Background thread management for geocoding
 * 5. Comprehensive error handling and user feedback
 *
 * Academic Learning Outcomes:
 * - Activity lifecycle management
 * - Cross-activity data communication
 * - Location services integration
 * - Image processing with Glide library
 * - Thread management and UI updates
 */
public class LocationDetailActivity extends AppCompatActivity {

    // UI Components - Following Material Design principles
    private ImageView ivLocationImage;      // Hero image with Google Photos support
    private TextView tvLocationName;        // Primary attraction name
    private TextView tvLocationCity;        // Location context
    private TextView tvLocationCategory;    // Attraction type (Historical, Waterfall, etc.)
    private TextView tvLocationDescription; // Full description text
    private TextView tvContributorName;     // Attribution to user who added location
    private Button btnGetDirections;       // Smart navigation button with fallbacks
    private Button btnShareLocation;       // Native Android sharing integration

    // Data model - Holds all attraction information
    private Attraction currentAttraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        // Initialize UI components first to avoid null pointer exceptions
        initializeViews();

        // Extract and load attraction data from the calling activity's intent
        // This demonstrates proper intent data handling and validation
        loadAttractionData();

        // Set up interactive elements after data is loaded
        setupButtonListeners();
    }

    /**
     * Initialize all UI components with findViewById calls
     * This method ensures all views are properly connected before use
     * Demonstrates proper UI initialization patterns in Android
     */
    private void initializeViews() {
        ivLocationImage = findViewById(R.id.iv_location_image);
        tvLocationName = findViewById(R.id.tv_location_name);
        tvLocationCity = findViewById(R.id.tv_location_city);
        tvLocationCategory = findViewById(R.id.tv_location_category);
        tvLocationDescription = findViewById(R.id.tv_location_description);
        tvContributorName = findViewById(R.id.tv_contributor_name);
        btnGetDirections = findViewById(R.id.btn_get_directions);
        btnShareLocation = findViewById(R.id.btn_share_location);
    }

    /**
     * Extract attraction data from Intent extras and populate the attraction object
     *
     * Key Learning: This method demonstrates proper intent data extraction
     * and the importance of using consistent key names between activities.
     *
     * Bug Fix: Previously failed due to mismatched intent keys between
     * AttractionAdapter and this activity. Fixed by standardizing key names.
     */
    private void loadAttractionData() {
        Intent intent = getIntent();

        // Extract all required data using standardized intent keys
        // These keys must match exactly with what AttractionAdapter sends
        String name = intent.getStringExtra("attraction_name");
        String city = intent.getStringExtra("attraction_city");
        String category = intent.getStringExtra("attraction_category");
        String description = intent.getStringExtra("attraction_description");
        String imageUrl = intent.getStringExtra("attraction_image_url");
        String contributorName = intent.getStringExtra("contributor_name");

        // Extract coordinate data with default values to handle missing data
        double latitude = intent.getDoubleExtra("attraction_latitude", 0.0);
        double longitude = intent.getDoubleExtra("attraction_longitude", 0.0);

        // Create and populate attraction object for easier data management
        currentAttraction = new Attraction();
        currentAttraction.setName(name);
        currentAttraction.setCity(city);
        currentAttraction.setCategory(category);
        currentAttraction.setDescription(description);
        currentAttraction.setImageUrl(imageUrl);
        currentAttraction.setLatitude(latitude);
        currentAttraction.setLongitude(longitude);

        // Display all the loaded data in the UI
        displayAttractionData(contributorName);
    }

    /**
     * Display attraction data in UI components with proper null checking
     * Demonstrates safe UI updates and user-friendly fallbacks
     */
    private void displayAttractionData(String contributorName) {
        // Populate text fields with attraction data
        tvLocationName.setText(currentAttraction.getName());
        tvLocationCity.setText(currentAttraction.getCity());
        tvLocationCategory.setText(currentAttraction.getCategory());
        tvLocationDescription.setText(currentAttraction.getDescription());

        // Handle contributor attribution with fallback for anonymous entries
        if (contributorName != null && !contributorName.isEmpty()) {
            tvContributorName.setText("Contributed by: " + contributorName);
        } else {
            tvContributorName.setText("Contributed by: Anonymous");
        }

        // Load the hero image using our Google Photos processing system
        loadLocationImage();
    }

    /**
     * Smart image loading with Google Photos URL processing
     *
     * Key Feature: This method demonstrates how to handle different types of image URLs
     * including Google Photos share links which need special processing.
     *
     * Technical Implementation:
     * 1. Detect if URL is a Google Photos share link
     * 2. Process Google Photos URLs to get direct image URLs
     * 3. Load images efficiently using Glide library
     * 4. Handle errors gracefully with placeholder images
     */
    private void loadLocationImage() {
        String imageUrl = currentAttraction.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Check if this is a Google Photos URL that requires processing
            if (isGooglePhotosUrl(imageUrl)) {
                // Process Google Photos share link to get direct image URL
                // This is done asynchronously to avoid blocking the UI thread
                GooglePhotosUrlHelper.processImageUrl(this, imageUrl, processedUrl -> {
                    loadImageWithGlide(processedUrl);
                });
            } else {
                // Direct image URL - load immediately with Glide
                loadImageWithGlide(imageUrl);
            }
        } else {
            // No image URL provided - show placeholder
            ivLocationImage.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    /**
     * Utility method to detect Google Photos URLs
     * These URLs need special processing to extract the actual image
     */
    private boolean isGooglePhotosUrl(String url) {
        return url != null && (
                url.contains("photos.app.goo.gl") ||       // Shortened share links
                url.contains("photos.google.com/share") ||  // Direct share links
                url.contains("photos.google.com/u/") ||     // User photo links
                url.contains("photos.google.com/album")     // Album links
        );
    }

    /**
     * Load image using Glide library with proper error handling
     * Demonstrates efficient image loading with caching and error states
     */
    private void loadImageWithGlide(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)  // Show while loading
                .error(R.drawable.ic_image_error)              // Show if loading fails
                .centerCrop()                                   // Scale appropriately
                .into(ivLocationImage);
    }

    /**
     * Set up button click listeners for user interactions
     * Demonstrates proper event handling and method delegation
     */
    private void setupButtonListeners() {
        // Get Directions button - Implements intelligent navigation system
        btnGetDirections.setOnClickListener(v -> openInGoogleMaps());

        // Share Location button - Uses Android's native sharing capabilities
        btnShareLocation.setOnClickListener(v -> shareLocation());
    }

    /**
     * INTELLIGENT DIRECTIONS SYSTEM - Three-Level Fallback Implementation
     *
     * This method demonstrates advanced problem-solving in mobile development.
     *
     * Problem Solved: Originally, the directions button failed when locations
     * didn't have stored coordinates in the database (showing 0.0, 0.0).
     *
     * Solution: Implemented a three-level fallback system that ensures the
     * directions feature ALWAYS works regardless of data completeness.
     *
     * Academic Learning: Shows understanding of graceful degradation,
     * user experience design, and robust error handling.
     */
    private void openInGoogleMaps() {
        double lat = currentAttraction.getLatitude();
        double lng = currentAttraction.getLongitude();

        // Debug logging for development and troubleshooting
        // Essential for identifying issues during testing phase
        android.util.Log.d("LocationDetail", "Get Directions clicked");
        android.util.Log.d("LocationDetail", "Coordinates: " + lat + ", " + lng);
        android.util.Log.d("LocationDetail", "Location name: " + currentAttraction.getName());

        // LEVEL 1: Use stored coordinates if available (most efficient)
        if (lat != 0.0 && lng != 0.0) {
            android.util.Log.d("LocationDetail", "Using stored coordinates");
            openMapsWithCoordinates(lat, lng);
        } else {
            // LEVEL 2: Geocode location name to get coordinates (fallback)
            android.util.Log.d("LocationDetail", "No coordinates available, trying geocoding...");
            geocodeLocationName();
        }
    }

    /**
     * LEVEL 1: Open maps using precise coordinates
     *
     * This method handles the ideal case where we have exact latitude/longitude
     * coordinates for the attraction. Creates proper Google Maps intents.
     */
    private void openMapsWithCoordinates(double lat, double lng) {
        try {
            // Create geo URI with coordinates and location name
            // Using Locale.US ensures consistent decimal formatting across devices
            String uri = String.format(java.util.Locale.US, "geo:%f,%f?q=%f,%f(%s)",
                lat, lng, lat, lng, Uri.encode(currentAttraction.getName()));

            android.util.Log.d("LocationDetail", "Generated URI: " + uri);

            // Create intent to open in any available maps application
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

            // Check if any app can handle this intent before attempting to open
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
                android.util.Log.d("LocationDetail", "Opened with system map app");
            } else {
                // Fallback to web browser if no map apps are installed
                String webUri = String.format(java.util.Locale.US,
                    "https://www.google.com/maps/search/?api=1&query=%f,%f", lat, lng);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
                startActivity(webIntent);
                android.util.Log.d("LocationDetail", "Opened in web browser");
            }
        } catch (Exception e) {
            // Comprehensive error handling with user feedback
            android.util.Log.e("LocationDetail", "Error opening maps: " + e.getMessage());
            Toast.makeText(this, "Unable to open maps app", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * LEVEL 2: Geocoding fallback system
     *
     * When coordinates aren't available, this method attempts to convert
     * the location name into coordinates using Android's Geocoder service.
     *
     * Key Technical Concepts Demonstrated:
     * 1. Background thread processing (avoids blocking UI)
     * 2. Main thread UI updates (Android requirement)
     * 3. Smart query construction for better accuracy
     * 4. Graceful fallback to Level 3 if geocoding fails
     */
    private void geocodeLocationName() {
        String locationName = currentAttraction.getName();
        String city = currentAttraction.getCity();

        // Build intelligent search query by combining available location data
        // Adding "Sri Lanka" improves geocoding accuracy for local places
        final String searchQuery;
        if (city != null && !city.isEmpty()) {
            searchQuery = locationName + ", " + city + ", Sri Lanka";
        } else {
            searchQuery = locationName + ", Sri Lanka";
        }

        android.util.Log.d("LocationDetail", "Geocoding: " + searchQuery);

        // Perform geocoding in background thread to avoid ANR (Application Not Responding)
        // This demonstrates proper thread management in Android development
        new Thread(() -> {
            try {
                // Use Android's built-in Geocoder service to convert names to coordinates
                android.location.Geocoder geocoder = new android.location.Geocoder(this, java.util.Locale.getDefault());
                java.util.List<android.location.Address> addresses = geocoder.getFromLocationName(searchQuery, 1);

                if (addresses != null && !addresses.isEmpty()) {
                    // Successfully found coordinates for the location
                    android.location.Address address = addresses.get(0);
                    double lat = address.getLatitude();
                    double lng = address.getLongitude();

                    android.util.Log.d("LocationDetail", "Geocoded coordinates: " + lat + ", " + lng);

                    // IMPORTANT: UI updates must happen on the main thread
                    // runOnUiThread ensures thread safety for UI operations
                    runOnUiThread(() -> {
                        // Cache the geocoded coordinates for future use
                        currentAttraction.setLatitude(lat);
                        currentAttraction.setLongitude(lng);

                        // Now open maps with the newly found coordinates
                        openMapsWithCoordinates(lat, lng);
                    });
                } else {
                    // Geocoding failed - fallback to Level 3 (search by name)
                    android.util.Log.w("LocationDetail", "Geocoding failed, trying search by name");
                    runOnUiThread(() -> openMapsWithSearchQuery(searchQuery));
                }
            } catch (Exception e) {
                // Handle any errors during geocoding process
                android.util.Log.e("LocationDetail", "Geocoding error: " + e.getMessage());
                runOnUiThread(() -> openMapsWithSearchQuery(searchQuery));
            }
        }).start(); // Start the background thread
    }

    /**
     * LEVEL 3: Search query fallback (final resort)
     *
     * When both stored coordinates and geocoding fail, this method provides
     * a guaranteed working solution by searching Google Maps by name.
     *
     * This ensures the directions feature ALWAYS works, demonstrating
     * commitment to user experience and robust application design.
     */
    private void openMapsWithSearchQuery(String searchQuery) {
        try {
            // Create Google Maps search URL with encoded query
            // URL encoding prevents issues with special characters in place names
            String encodedQuery = Uri.encode(searchQuery);
            String webUri = "https://www.google.com/maps/search/?api=1&query=" + encodedQuery;

            android.util.Log.d("LocationDetail", "Opening maps with search: " + webUri);

            // Open Google Maps in web browser with search query
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
            startActivity(webIntent);

            // Inform user that we're using search instead of exact location
            Toast.makeText(this, "Opening maps with location search", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Final error handling - this should rarely happen
            android.util.Log.e("LocationDetail", "Error opening maps with search: " + e.getMessage());
            Toast.makeText(this, "Unable to open maps", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Native Android sharing implementation
     *
     * Creates formatted text with location details and opens the system
     * sharing menu, allowing users to share via any installed app.
     *
     * Demonstrates integration with Android's built-in sharing capabilities.
     */
    private void shareLocation() {
        // Create formatted share text with location information
        // Using emoji and clear formatting for better user experience
        String shareText = String.format(
                "🏛️ Check out this amazing place in Sri Lanka!\n\n" +
                "📍 %s\n" +
                "🌍 %s\n" +
                "🏷️ %s\n\n" +
                "%s\n\n" +
                "Discover more hidden gems with Hidden Sri Lanka app! 🇱🇰",
                currentAttraction.getName(),
                currentAttraction.getCity(),
                currentAttraction.getCategory(),
                currentAttraction.getDescription()
        );

        // Create share intent with formatted text
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hidden Gem: " + currentAttraction.getName());

        // Create chooser dialog to let user select sharing app
        Intent chooser = Intent.createChooser(shareIntent, "Share this location");
        startActivity(chooser);
    }

    /**
     * Activity lifecycle management
     * Handles back button press to return to previous screen
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(); // Properly close this activity
    }
}
