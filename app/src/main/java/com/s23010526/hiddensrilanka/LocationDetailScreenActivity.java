package com.s23010526.hiddensrilanka;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
        if (chipCategory != null && attractionCategory != null) {
            chipCategory.setText(attractionCategory);
        }

        // Set contributor information
        if (tvContributor != null && contributorName != null) {
            tvContributor.setText(getString(R.string.contributed_by, contributorName));
        }

        // Set contributed date
        if (tvContributedDate != null && contributedAt > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(contributedAt));
            tvContributedDate.setText(getString(R.string.added_on, formattedDate));
        }

        // Setup main image and image gallery
        setupImageGallery();

        // Show/hide YouTube button based on availability
        if (btnWatchVideo != null) {
            if (youtubeUrl != null && !youtubeUrl.trim().isEmpty()) {
                btnWatchVideo.setVisibility(View.VISIBLE);
            } else {
                btnWatchVideo.setVisibility(View.GONE);
            }
        }
    }

    private void setupImageGallery() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Set main image (first image)
            if (ivMainImage != null) {
                Glide.with(this)
                        .load(imageUrls.get(0))
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_placeholder)
                        .centerCrop()
                        .into(ivMainImage);
            }

            // Setup image gallery if more than one image
            if (imageUrls.size() > 1) {
                setupImageGalleryRecyclerView();
                rvImageGallery.setVisibility(View.VISIBLE);
            } else {
                rvImageGallery.setVisibility(View.GONE);
            }
        } else {
            // No images available
            if (ivMainImage != null) {
                ivMainImage.setImageResource(R.drawable.ic_image_placeholder);
            }
            rvImageGallery.setVisibility(View.GONE);
        }
    }

    private void setupImageGalleryRecyclerView() {
        // Create a copy of imageUrls starting from the second image (index 1)
        ArrayList<String> galleryImages = new ArrayList<>();
        if (imageUrls.size() > 1) {
            for (int i = 1; i < imageUrls.size(); i++) {
                galleryImages.add(imageUrls.get(i));
            }
        }

        // Setup RecyclerView with horizontal layout
        rvImageGallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Create adapter (false = don't show remove buttons)
        ImageGalleryAdapter galleryAdapter = new ImageGalleryAdapter(this, galleryImages, false);
        galleryAdapter.setOnImageClickListener(new ImageGalleryAdapter.OnImageClickListener() {
            @Override
            public void onImageClick(int position, String imageUrl) {
                // When gallery image is clicked, make it the main image
                setMainImage(imageUrl);
            }

            @Override
            public void onImageRemove(int position) {
                // Not used in detail view (remove buttons are hidden)
            }
        });

        rvImageGallery.setAdapter(galleryAdapter);
    }

    private void setMainImage(String imageUrl) {
        if (ivMainImage != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .centerCrop()
                    .into(ivMainImage);
        }
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
            } else {
                // Fallback to web browser
                Uri webUri = Uri.parse("https://www.google.com/maps/search/" + Uri.encode(query));
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                startActivity(webIntent);
            }
        } catch (Exception e) {
            // Log error instead of showing toast to user
            Log.e(TAG, "Unable to open maps: " + e.getMessage());
        }
    }

    private void shareLocation() {
        try {
            String shareText = String.format(
                    "🏛️ Check out this amazing place in Sri Lanka!\n\n" +
                    "📍 %s\n" +
                    "🌍 %s\n" +
                    "🏷️ %s\n\n" +
                    "%s\n\n" +
                    "Discover more hidden gems with Hidden Sri Lanka app! 🇱🇰",
                    attractionName, cityName, attractionCategory, attractionDescription
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Hidden Gem: " + attractionName);

            Intent chooser = Intent.createChooser(shareIntent, "Share this location");
            startActivity(chooser);
        } catch (Exception e) {
            // Log error instead of showing toast to user
            Log.e(TAG, "Unable to share location: " + e.getMessage());
        }
    }

    private void watchYouTubeVideo() {
        if (youtubeUrl == null || youtubeUrl.isEmpty()) {
            // Simply hide the button or disable it instead of showing toast
            btnWatchVideo.setVisibility(View.GONE);
            return;
        }

        try {
            // Try to open in YouTube app first
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
            youtubeIntent.setPackage("com.google.android.youtube");
            if (youtubeIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(youtubeIntent);
            } else {
                // Fallback to web browser
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                startActivity(webIntent);
            }
        } catch (Exception e) {
            // Log error instead of showing toast to user
            Log.e(TAG, "Unable to open video: " + e.getMessage());
        }
    }
}
