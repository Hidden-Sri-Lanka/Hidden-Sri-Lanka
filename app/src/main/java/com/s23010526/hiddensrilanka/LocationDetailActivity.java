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
 * Features for Viva Explanation:
 * 1. Image Loading - Uses Glide with Google Photos URLs
 * 2. Location Details - Name, description, category, city
 * 3. Google Maps Integration - Opens location in Google Maps
 * 4. Share Functionality - Share location with others
 * 5. Contributor Attribution - Shows who added the location
 */
public class LocationDetailActivity extends AppCompatActivity {

    // UI Components
    private ImageView ivLocationImage;
    private TextView tvLocationName;
    private TextView tvLocationCity;
    private TextView tvLocationCategory;
    private TextView tvLocationDescription;
    private TextView tvContributorName;
    private Button btnGetDirections;
    private Button btnShareLocation;

    // Location data
    private Attraction currentAttraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        // Initialize UI components
        initializeViews();

        // Get attraction data from intent
        loadAttractionData();

        // Set up button listeners
        setupButtonListeners();
    }

    /**
     * Initialize all UI components
     * Simple and clear for viva explanation
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
     * Load attraction data from intent extras
     * Demonstrates data passing between activities
     */
    private void loadAttractionData() {
        Intent intent = getIntent();

        // Extract attraction data
        String name = intent.getStringExtra("attraction_name");
        String city = intent.getStringExtra("attraction_city");
        String category = intent.getStringExtra("attraction_category");
        String description = intent.getStringExtra("attraction_description");
        String imageUrl = intent.getStringExtra("attraction_image_url");
        String contributorName = intent.getStringExtra("contributor_name");
        double latitude = intent.getDoubleExtra("attraction_latitude", 0.0);
        double longitude = intent.getDoubleExtra("attraction_longitude", 0.0);

        // Create attraction object for easier management
        currentAttraction = new Attraction();
        currentAttraction.setName(name);
        currentAttraction.setCity(city);
        currentAttraction.setCategory(category);
        currentAttraction.setDescription(description);
        currentAttraction.setImageUrl(imageUrl);
        currentAttraction.setLatitude(latitude);
        currentAttraction.setLongitude(longitude);

        // Display the data
        displayAttractionData(contributorName);
    }

    /**
     * Display attraction data in UI components
     * Uses your consistent design system
     */
    private void displayAttractionData(String contributorName) {
        // Set text data
        tvLocationName.setText(currentAttraction.getName());
        tvLocationCity.setText(currentAttraction.getCity());
        tvLocationCategory.setText(currentAttraction.getCategory());
        tvLocationDescription.setText(currentAttraction.getDescription());

        // Set contributor name
        if (contributorName != null && !contributorName.isEmpty()) {
            tvContributorName.setText("Contributed by: " + contributorName);
        } else {
            tvContributorName.setText("Contributed by: Anonymous");
        }

        // Load image using Google Photos URL
        loadLocationImage();
    }

    /**
     * Load location image using Glide
     * Demonstrates efficient image loading with error handling
     */
    private void loadLocationImage() {
        String imageUrl = currentAttraction.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .centerCrop()
                    .into(ivLocationImage);
        } else {
            // Show placeholder if no image
            ivLocationImage.setImageResource(R.drawable.ic_image_placeholder);
        }
    }

    /**
     * Set up button click listeners
     * Implements Google Maps integration and sharing
     */
    private void setupButtonListeners() {
        // Get Directions button - Opens Google Maps
        btnGetDirections.setOnClickListener(v -> openInGoogleMaps());

        // Share Location button - Shares location details
        btnShareLocation.setOnClickListener(v -> shareLocation());
    }

    /**
     * Opens location in Google Maps app
     * Creates intent with latitude/longitude coordinates
     */
    private void openInGoogleMaps() {
        double lat = currentAttraction.getLatitude();
        double lng = currentAttraction.getLongitude();

        if (lat != 0.0 && lng != 0.0) {
            // Create Google Maps URI
            String uri = String.format("geo:%f,%f?q=%f,%f(%s)",
                    lat, lng, lat, lng, currentAttraction.getName());

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapIntent.setPackage("com.google.android.apps.maps");

            // Check if Google Maps is installed
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Fallback to web browser
                String webUri = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f", lat, lng);
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUri));
                startActivity(webIntent);
            }
        } else {
            Toast.makeText(this, "Location coordinates not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Share location details with other apps
     * Creates shareable text with location information
     */
    private void shareLocation() {
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

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hidden Gem: " + currentAttraction.getName());

        // Create chooser dialog
        Intent chooser = Intent.createChooser(shareIntent, "Share this location");
        startActivity(chooser);
    }

    /**
     * Handle back button - returns to home screen
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
