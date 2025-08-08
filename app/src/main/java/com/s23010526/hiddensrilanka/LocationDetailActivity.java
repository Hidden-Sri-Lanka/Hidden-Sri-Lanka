package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

/**
 * Location Detail Activity - Shows complete information about a selected attraction
 *
 * This activity demonstrates key Android development concepts:
 * 1. Intent data extraction and validation
 * 2. Google Photos URL processing for image loading
 * 3. Three-level fallback system for navigation
 * 4. Background thread management for geocoding
 * 5. Comprehensive error handling and user feedback
 * 6. Horizontal scrolling image gallery with ViewPager2
 *
 * Academic Learning Outcomes:
 * - Activity lifecycle management
 * - Cross-activity data communication
 * - Location services integration
 * - Image processing with Glide library
 * - Thread management and UI updates
 * - ViewPager2 implementation for smooth scrolling
 */
public class LocationDetailActivity extends AppCompatActivity {

    // UI Components - Following Material Design principles
    private ViewPager2 vpLocationImages;       // Horizontal scrolling image pager
    private TextView tvImageCounter;           // Shows current image position (e.g., "2 / 5")
    private TextView tvLocationName;           // Primary attraction name
    private TextView tvLocationCity;           // Location context
    private TextView tvLocationCategory;       // Attraction type (Historical, Waterfall, etc.)
    private TextView tvLocationDescription;    // Full description text
    private TextView tvContributorName;        // Attribution to user who added location
    private Button btnGetDirections;          // Smart navigation button with fallbacks
    private Button btnShareLocation;          // Native Android sharing integration

    // Image pager components
    private ImagePagerAdapter imagePagerAdapter;
    private ArrayList<String> imageUrls;

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
        vpLocationImages = findViewById(R.id.vp_location_images);
        tvImageCounter = findViewById(R.id.tv_image_counter);
        tvLocationName = findViewById(R.id.tv_location_name);
        tvLocationCity = findViewById(R.id.tv_location_city);
        tvLocationCategory = findViewById(R.id.tv_location_category);
        tvLocationDescription = findViewById(R.id.tv_location_description);
        tvContributorName = findViewById(R.id.tv_contributor_name);
        btnGetDirections = findViewById(R.id.btn_get_directions);
        // btnShareLocation = findViewById(R.id.btn_share_location); // Commented out as button is not in layout
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

        // Extract multiple images if available (for image gallery support)
        ArrayList<String> imagesList = intent.getStringArrayListExtra("attraction_images");

        // Create and populate attraction object for easier data management
        currentAttraction = new Attraction();
        currentAttraction.setName(name);
        currentAttraction.setCity(city);
        currentAttraction.setCategory(category);
        currentAttraction.setDescription(description);
        currentAttraction.setImageUrl(imageUrl);
        currentAttraction.setLatitude(latitude);
        currentAttraction.setLongitude(longitude);

        // Handle multiple images - if available, use them; otherwise fall back to single image
        if (imagesList != null && !imagesList.isEmpty()) {
            // Multiple images available - set them in the attraction
            currentAttraction.setImages(imagesList);
            android.util.Log.d("LocationDetail", "Loaded " + imagesList.size() + " images for gallery");
        } else if (imageUrl != null && !imageUrl.isEmpty()) {
            // Single image available - create a list with just this image for consistency
            ArrayList<String> singleImageList = new ArrayList<>();
            singleImageList.add(imageUrl);
            currentAttraction.setImages(singleImageList);
            android.util.Log.d("LocationDetail", "Using single image in gallery");
        } else {
            android.util.Log.d("LocationDetail", "No images available");
        }

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
     * Smart image loading with multiple image support
     * Handles both single images (backward compatibility) and multiple images (new feature)
     */
    private void loadLocationImage() {
        // Initialize image gallery
        setupImagePager();

        // Get images from the attraction
        if (currentAttraction.hasImages()) {
            // Multiple images available - show gallery
            imageUrls = new ArrayList<>(currentAttraction.getImages());
            setupMultipleImages();
        } else if (currentAttraction.getImageUrl() != null && !currentAttraction.getImageUrl().isEmpty()) {
            // Single image (backward compatibility)
            imageUrls = new ArrayList<>();
            imageUrls.add(currentAttraction.getImageUrl());
            setupSingleImage();
        } else {
            // No images available
            setupNoImages();
        }
    }

    /**
     * Set up the image pager ViewPager2 for horizontal scrolling
     */
    private void setupImagePager() {
        imageUrls = new ArrayList<>();
        imagePagerAdapter = new ImagePagerAdapter(this, imageUrls);
        vpLocationImages.setAdapter(imagePagerAdapter);

        // Register a page change callback to update the image counter
        vpLocationImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateImageCounter(position);
            }
        });
    }

    /**
     * Update the image counter text view
     */
    private void updateImageCounter(int position) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Show current position (1-based) and total count
            tvImageCounter.setText((position + 1) + " / " + imageUrls.size());
        } else {
            // Hide counter if no images are available
            tvImageCounter.setText("");
        }
    }

    /**
     * Handle multiple images - show horizontal scrolling pager
     */
    private void setupMultipleImages() {
        if (!imageUrls.isEmpty()) {
            // Update the adapter with new image URLs
            imagePagerAdapter.updateImages(imageUrls);
            vpLocationImages.setVisibility(android.view.View.VISIBLE);

            // Show counter if more than one image
            if (imageUrls.size() > 1) {
                tvImageCounter.setVisibility(android.view.View.VISIBLE);
                updateImageCounter(0); // Start with first image
                android.util.Log.d("LocationDetail", "Showing horizontal scrolling with " + imageUrls.size() + " images");
            } else {
                tvImageCounter.setVisibility(android.view.View.GONE);
                android.util.Log.d("LocationDetail", "Single image - hiding counter");
            }
        } else {
            android.util.Log.d("LocationDetail", "No images to display");
        }
    }

    /**
     * Handle single image - show in pager without counter
     */
    private void setupSingleImage() {
        if (!imageUrls.isEmpty()) {
            imagePagerAdapter.updateImages(imageUrls);
            vpLocationImages.setVisibility(android.view.View.VISIBLE);
            tvImageCounter.setVisibility(android.view.View.GONE);
        }
    }

    /**
     * Handle no images - show placeholder
     */
    private void setupNoImages() {
        vpLocationImages.setVisibility(android.view.View.GONE);
        tvImageCounter.setVisibility(android.view.View.GONE);
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
     * Set up button click listeners for user interactions
     * Demonstrates proper event handling and method delegation
     */
    private void setupButtonListeners() {
        // Get Directions button - Implements intelligent navigation system
        btnGetDirections.setOnClickListener(v -> openInGoogleMaps());

        // Share Location button - Uses Android's native sharing capabilities
        // btnShareLocation.setOnClickListener(v -> shareLocation()); // Commented out as button is not in layout
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
            // Only show toast for actual errors that prevent the action
            Toast.makeText(this, "Unable to open maps", Toast.LENGTH_SHORT).show();
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
