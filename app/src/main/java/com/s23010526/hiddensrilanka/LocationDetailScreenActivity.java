package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LocationDetailScreenActivity extends BaseActivity {

    private static final String TAG = "LocationDetailScreen";

    // UI Components
    private ImageView ivMainImage;
    private TextView tvLocationName, tvCategory, tvDescription, tvContributor, tvContributedDate;
    private Chip chipCategory;
    private MaterialButton btnGetDirections, btnShareLocation, btnWatchVideo;
    private RecyclerView rvImageGallery;

    // Data
    private String attractionName, attractionCategory, attractionDescription;
    private String contributorName, youtubeUrl, cityName;
    private long contributedAt;
    private ArrayList<String> imageUrls;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_location_detail_screen;
    }

    @Override
    protected String getActivityTitle() {
        return "Location Details";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();
        loadDataFromIntent();
        setupClickListeners();
        displayLocationDetails();
    }

    private void initializeViews() {
        ivMainImage = findViewById(R.id.iv_main_image);
        tvLocationName = findViewById(R.id.tv_location_name);
        tvCategory = findViewById(R.id.tv_category);
        tvDescription = findViewById(R.id.tv_description);
        tvContributor = findViewById(R.id.tv_contributor);
        tvContributedDate = findViewById(R.id.tv_contributed_date);
        chipCategory = findViewById(R.id.chip_category);
        btnGetDirections = findViewById(R.id.btn_get_directions);
        btnShareLocation = findViewById(R.id.btn_share_location);
        btnWatchVideo = findViewById(R.id.btn_watch_video);
        rvImageGallery = findViewById(R.id.rv_image_gallery);
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        attractionName = intent.getStringExtra("ATTRACTION_NAME");
        attractionCategory = intent.getStringExtra("ATTRACTION_CATEGORY");
        attractionDescription = intent.getStringExtra("ATTRACTION_DESCRIPTION");
        contributorName = intent.getStringExtra("ATTRACTION_CONTRIBUTOR");
        contributedAt = intent.getLongExtra("ATTRACTION_CONTRIBUTED_AT", 0);
        youtubeUrl = intent.getStringExtra("ATTRACTION_YOUTUBE_URL");
        cityName = intent.getStringExtra("ATTRACTION_CITY");
        imageUrls = intent.getStringArrayListExtra("ATTRACTION_IMAGES");

        Log.d(TAG, "Loaded attraction data: " + attractionName);
    }

    private void setupClickListeners() {
        btnGetDirections.setOnClickListener(v -> openGoogleMaps());
        btnShareLocation.setOnClickListener(v -> shareLocation());
        btnWatchVideo.setOnClickListener(v -> watchYouTubeVideo());
    }

    private void displayLocationDetails() {
        // Set basic information
        tvLocationName.setText(attractionName != null ? attractionName : "Unknown Location");
        tvCategory.setText(attractionCategory != null ? attractionCategory : "Unknown Category");
        tvDescription.setText(attractionDescription != null ? attractionDescription : "No description available.");

        // Set category chip
        if (attractionCategory != null) {
            chipCategory.setText(attractionCategory);
            chipCategory.setVisibility(View.VISIBLE);
        } else {
            chipCategory.setVisibility(View.GONE);
        }

        // Set contributor information
        if (contributorName != null && !contributorName.isEmpty() && !contributorName.equals("Unknown")) {
            tvContributor.setText("Contributed by: " + contributorName);
            tvContributor.setVisibility(View.VISIBLE);
        } else {
            tvContributor.setVisibility(View.GONE);
        }

        // Set contribution date
        if (contributedAt > 0) {
            String formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    .format(new Date(contributedAt));
            tvContributedDate.setText("Added on: " + formattedDate);
            tvContributedDate.setVisibility(View.VISIBLE);
        } else {
            tvContributedDate.setVisibility(View.GONE);
        }

        // Load main image
        if (imageUrls != null && !imageUrls.isEmpty()) {
            String mainImageUrl = imageUrls.get(0);
            Glide.with(this)
                    .load(mainImageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(ivMainImage);

            // Setup image gallery if multiple images
            if (imageUrls.size() > 1) {
                setupImageGallery();
            } else {
                rvImageGallery.setVisibility(View.GONE);
            }
        } else {
            ivMainImage.setImageResource(R.drawable.ic_image_placeholder);
            rvImageGallery.setVisibility(View.GONE);
        }

        // Show/hide YouTube button
        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            btnWatchVideo.setVisibility(View.VISIBLE);
        } else {
            btnWatchVideo.setVisibility(View.GONE);
        }
    }

    private void setupImageGallery() {
        // Create a horizontal RecyclerView for additional images
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvImageGallery.setLayoutManager(layoutManager);

        // You can create an ImageGalleryAdapter here if needed
        // For now, we'll keep it simple
        rvImageGallery.setVisibility(View.VISIBLE);
    }

    private void openGoogleMaps() {
        try {
            // Create a search query for Google Maps
            String query = attractionName;
            if (cityName != null && !cityName.isEmpty()) {
                query += ", " + cityName + ", Sri Lanka";
            } else {
                query += ", Sri Lanka";
            }

            // Create Google Maps intent
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Check if Google Maps app is installed
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
                Toast.makeText(this, "Opening in Google Maps", Toast.LENGTH_SHORT).show();
            } else {
                // Fallback to web browser
                Uri webUri = Uri.parse("https://www.google.com/maps/search/" + Uri.encode(query));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                startActivity(webIntent);
                Toast.makeText(this, "Opening in browser", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening Google Maps: " + e.getMessage());
            Toast.makeText(this, "Unable to open maps", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareLocation() {
        try {
            String shareText = "📍 Check out this amazing place in Sri Lanka!\n\n" +
                             "🏛️ " + (attractionName != null ? attractionName : "Unknown Location") + "\n" +
                             "📂 Category: " + (attractionCategory != null ? attractionCategory : "Unknown") + "\n\n" +
                             "📝 " + (attractionDescription != null ?
                                     (attractionDescription.length() > 100 ?
                                      attractionDescription.substring(0, 100) + "..." :
                                      attractionDescription) : "No description available") + "\n\n" +
                             "🤝 Contributed by: " + (contributorName != null && !contributorName.isEmpty() ? contributorName : "Community") + "\n\n" +
                             "🔍 Search '" + (attractionName != null ? attractionName : "this location") +
                             (cityName != null ? " " + cityName : "") + "' on Google Maps\n\n" +
                             "📱 Shared via Hidden Sri Lanka App";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Discover " + (attractionName != null ? attractionName : "this amazing place") + " in Sri Lanka");

            startActivity(Intent.createChooser(shareIntent, "Share this location via"));
            Toast.makeText(this, "Sharing location details", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error sharing location: " + e.getMessage());
            Toast.makeText(this, "Unable to share location", Toast.LENGTH_SHORT).show();
        }
    }

    private void watchYouTubeVideo() {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            Toast.makeText(this, "No video available for this location", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Try to open in YouTube app first
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
            youtubeIntent.setPackage("com.google.android.youtube");

            if (youtubeIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(youtubeIntent);
                Toast.makeText(this, "Opening in YouTube app", Toast.LENGTH_SHORT).show();
            } else {
                // Fallback to web browser
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(webIntent);
                Toast.makeText(this, "Opening in browser", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening YouTube video: " + e.getMessage());
            Toast.makeText(this, "Unable to open video", Toast.LENGTH_SHORT).show();
        }
    }
}
