package com.s23010526.hiddensrilanka;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddLocationActivity extends BaseActivity implements ImageGalleryAdapter.OnImageClickListener {

    private static final String TAG = "AddLocationActivity";

    // UI Components
    private EditText etLocationName, etDescription, etYoutubeUrl, etContributorName, etImageUrl;
    private AutoCompleteTextView etCategory, etCity, etProvince;
    private ImageView ivTutorialThumbnail, btnPlayTutorial;
    private LinearLayout layoutGalleryPlaceholder;
    private MaterialButton btnSubmitLocation, btnLoadUrl, btnGooglePhotos, btnClearImages;
    private TextInputLayout tilCity;
    private ProgressBar progressBar;
    private RecyclerView rvImageGallery;

    // Tutorial video URL - Replace with your actual instructional video
    private static final String TUTORIAL_VIDEO_URL = "https://www.youtube.com/watch?v=YOUR_TUTORIAL_VIDEO_ID"; // Replace with actual tutorial video

    // Firebase
    private FirebaseFirestore firestore;

    // Image gallery data
    private ArrayList<String> imageUrls;
    private ImageGalleryAdapter imageGalleryAdapter;

    // Data structures for province-city mapping
    private Map<String, String[]> provinceCityMap;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_add_location;
    }

    @Override
    protected String getActivityTitle() {
        return "Add New Location";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeViews();
        initializeFirebase();
        initializeProvinceCityMapping();
        setupDropdowns();
        setupClickListeners();
        setupTutorialVideo();
    }

    private void initializeViews() {
        etLocationName = findViewById(R.id.et_location_name);
        etDescription = findViewById(R.id.et_description);
        etYoutubeUrl = findViewById(R.id.et_youtube_url);
        etContributorName = findViewById(R.id.et_contributor_name);
        etImageUrl = findViewById(R.id.et_image_url);
        etCategory = findViewById(R.id.et_category);
        etCity = findViewById(R.id.et_city);
        etProvince = findViewById(R.id.et_province);
        ivTutorialThumbnail = findViewById(R.id.iv_tutorial_thumbnail);
        btnPlayTutorial = findViewById(R.id.btn_play_tutorial);
        layoutGalleryPlaceholder = findViewById(R.id.layout_gallery_placeholder);
        btnSubmitLocation = findViewById(R.id.btn_submit_location);
        btnLoadUrl = findViewById(R.id.btn_load_url);
        btnGooglePhotos = findViewById(R.id.btn_google_photos);
        btnClearImages = findViewById(R.id.btn_clear_images);
        tilCity = findViewById(R.id.til_city);
        progressBar = findViewById(R.id.progress_bar_submit);

        // Initialize RecyclerView for image gallery
        rvImageGallery = findViewById(R.id.rv_image_gallery);
        rvImageGallery.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageUrls = new ArrayList<>();
        imageGalleryAdapter = new ImageGalleryAdapter(this, imageUrls, true); // true = show remove buttons
        imageGalleryAdapter.setOnImageClickListener(this);
        rvImageGallery.setAdapter(imageGalleryAdapter);

        updateGalleryVisibility();
    }

    private void initializeFirebase() {
        firestore = FirebaseFirestore.getInstance();
        Log.d(TAG, "Firestore initialized");
    }

    private void initializeProvinceCityMapping() {
        // Initialize province-city mapping
        provinceCityMap = new HashMap<>();
        provinceCityMap.put("Western Province", new String[]{
            "Colombo", "Gampaha", "Kalutara", "Negombo", "Panadura", "Moratuwa",
            "Sri Jayawardenepura Kotte", "Dehiwala", "Mount Lavinia", "Kelaniya",
            "Ja-Ela", "Wattala", "Peliyagoda", "Ragama", "Kaduwela", "Maharagama",
            "Kotte", "Battaramulla", "Homagama", "Padukka", "Hanwella", "Avissawella",
            "Beruwala", "Bentota", "Aluthgama", "Wadduwa", "Payagala"
        });
        provinceCityMap.put("Central Province", new String[]{
            "Kandy", "Matale", "Nuwara Eliya", "Gampola", "Nawalapitiya", "Hatton",
            "Talawakelle", "Nanu Oya", "Haputale", "Bandarawela", "Ella", "Welimada",
            "Badulla", "Passara", "Mahiyanganaya", "Diyatalawa", "Pussellawa",
            "Ramboda", "Maskeliya", "Norton Bridge", "Watawala", "Dikoya", "Bogawantalawa"
        });
        provinceCityMap.put("Southern Province", new String[]{
            "Galle", "Matara", "Hambantota", "Tangalle", "Mirissa", "Weligama",
            "Dikwella", "Tissamaharama", "Kataragama", "Embilipitiya", "Suriyawewa",
            "Ambalantota", "Beliatta", "Deniyaya", "Akuressa", "Kamburupitiya",
            "Kirinda", "Yala", "Bundala", "Unawatuna", "Hikkaduwa", "Bentota"
        });
        provinceCityMap.put("Northern Province", new String[]{
            "Jaffna", "Vavuniya", "Mannar", "Kilinochchi", "Mullaittivu", "Point Pedro",
            "Chavakachcheri", "Valvettithurai", "Kayts", "Karainagar", "Nallur",
            "Kondavil", "Tellippalai", "Kopay", "Udupiddy", "Elephant Pass"
        });
        provinceCityMap.put("Eastern Province", new String[]{
            "Trincomalee", "Batticaloa", "Ampara", "Kalmunai", "Akkaraipattu",
            "Sammanthurai", "Kattankudy", "Eravur", "Valaichchenai", "Pasikudah",
            "Kalkudah", "Arugam Bay", "Pottuvil", "Lahugala", "Siyambalanduwa",
            "Uhana", "Maha Oya", "Nintavur", "Addalachchenai", "Chenkalady"
        });
        provinceCityMap.put("North Western Province", new String[]{
            "Kurunegala", "Puttalam", "Chilaw", "Wariyapola", "Mawathagama",
            "Kuliyapitiya", "Narammala", "Pannala", "Alawwa", "Bingiriya",
            "Nikaweratiya", "Giriulla", "Polgahawela", "Kegalle", "Mawanella",
            "Warakapola", "Ruwanwella", "Yatiyantota", "Deraniyagala", "Kitulgala"
        });
        provinceCityMap.put("North Central Province", new String[]{
            "Anuradhapura", "Polonnaruwa", "Dambulla", "Sigiriya", "Habarana",
            "Mihintale", "Kekirawa", "Tambuttegama", "Galenbindunuwewa", "Eppawala",
            "Medawachchiya", "Rambewa", "Thambuttegama", "Galnewa", "Palagala",
            "Hingurakgoda", "Minneriya", "Medirigiriya", "Welikanda", "Dimbulagala"
        });
        provinceCityMap.put("Uva Province", new String[]{
            "Badulla", "Monaragala", "Bandarawela", "Ella", "Haputale", "Welimada",
            "Passara", "Mahiyanganaya", "Diyatalawa", "Hali Ela", "Demodara",
            "Namunukula", "Uva Paranagama", "Kandaketiya", "Haldummulla", "Hapugastenna",
            "Soranathota", "Meegahakiula", "Buttala", "Wellawaya", "Thanamalwila",
            "Kataragama", "Siyambalanduwa", "Madulla", "Bibile"
        });
        provinceCityMap.put("Sabaragamuwa Province", new String[]{
            "Ratnapura", "Kegalle", "Embilipitiya", "Balangoda", "Kahawatta",
            "Pelmadulla", "Eheliyagoda", "Kuruwita", "Godakawela", "Rakwana",
            "Nivitigala", "Kalawana", "Kolonne", "Weligepola", "Ayagama",
            "Imbulpe", "Lellopitiya", "Opanayaka", "Palmadulla", "Suriyawewa"
        });
    }

    private void setupDropdowns() {
        // Categories dropdown
        String[] categories = {
            "Historical Site", "WaterFall", "Beach", "Mountain", "Temple",
            "National Park", "Cave", "Lake", "Village", "Cultural Site", "More"
        };
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, categories);
        etCategory.setAdapter(categoryAdapter);

        // All Sri Lankan cities (comprehensive list)
        List<String> allCities = new ArrayList<>();
        for (String[] cities : provinceCityMap.values()) {
            for (String city : cities) {
                if (!allCities.contains(city)) {
                    allCities.add(city);
                }
            }
        }
        allCities.sort(String::compareToIgnoreCase);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, allCities);
        etCity.setAdapter(cityAdapter);
        etCity.setEnabled(true);

        // Provinces dropdown
        String[] provinces = {
            "Western Province", "Central Province", "Southern Province", "Northern Province",
            "Eastern Province", "North Western Province", "North Central Province", "Uva Province",
            "Sabaragamuwa Province"
        };
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, provinces);
        etProvince.setAdapter(provinceAdapter);
    }

    private void setupClickListeners() {
        btnSubmitLocation.setOnClickListener(v -> validateAndSubmitLocation());
        btnLoadUrl.setOnClickListener(v -> loadImageFromUrl());

        // Google Photos button click listener
        btnGooglePhotos.setOnClickListener(v -> openGooglePhotosHelper());

        // Clear Images button click listener
        btnClearImages.setOnClickListener(v -> clearImageGallery());

        // Province selection listener - optional filtering
        etProvince.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProvince = parent.getItemAtPosition(position).toString();
            updateCityDropdown(selectedProvince);
        });

        // Tutorial video play button
        btnPlayTutorial.setOnClickListener(v -> playTutorialVideo());
    }

    private void updateCityDropdown(String selectedProvince) {
        String[] cities = provinceCityMap.get(selectedProvince);
        if (cities != null) {
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cities);
            etCity.setAdapter(cityAdapter);
            etCity.setText("");

            tilCity.setHint("Select City from " + selectedProvince);
            Toast.makeText(this, "Cities filtered for " + selectedProvince, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGalleryVisibility() {
        if (imageUrls.isEmpty()) {
            layoutGalleryPlaceholder.setVisibility(View.VISIBLE);
            btnClearImages.setVisibility(View.GONE);
        } else {
            layoutGalleryPlaceholder.setVisibility(View.GONE);
            btnClearImages.setVisibility(View.VISIBLE);
        }
    }

    private void loadImageFromUrl() {
        String url = etImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(url)) {
            etImageUrl.setError("Please enter an image URL");
            return;
        }

        if (!isValidUrl(url)) {
            etImageUrl.setError("Please enter a valid URL");
            return;
        }

        // Add image to gallery
        imageUrls.add(url);
        imageGalleryAdapter.notifyItemInserted(imageUrls.size() - 1);
        updateGalleryVisibility();

        // Clear the input field
        etImageUrl.setText("");

        Toast.makeText(this, "Image added to gallery!", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void loadAndDisplayImage(String url) {
        Log.d(TAG, "loadAndDisplayImage called with URL: " + url);
        progressBar.setVisibility(View.VISIBLE);

        // Check if it's a Google Photos share link that needs processing
        if (url.contains("photos.app.goo.gl") || url.contains("photos.google.com/share")) {
            Log.d(TAG, "Detected Google Photos URL, processing...");
            Toast.makeText(this, "Processing Google Photos link...", Toast.LENGTH_SHORT).show();

            // Use the GooglePhotosUrlHelper to process the URL first
            GooglePhotosUrlHelper.processImageUrl(this, url, processedUrl -> {
                Log.d(TAG, "Google Photos URL processed to: " + processedUrl);
                // Load the processed URL
                loadImageWithGlide(processedUrl);
            });
        } else {
            Log.d(TAG, "Direct image URL detected, loading directly...");
            // Direct image URL - load directly
            loadImageWithGlide(url);
        }
    }

    private void loadImageWithGlide(String url) {
        Log.d(TAG, "loadImageWithGlide called with URL: " + url);

        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "Cannot load image: URL is empty");
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Error: Image URL is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Since Glide seems to be having consistent timeout issues,
        // let's provide a more direct user experience
        Log.d(TAG, "Attempting to load image with Glide...");

        // Show immediate feedback to user
        Toast.makeText(this, "Testing image URL...", Toast.LENGTH_SHORT).show();

        // Add a timeout handler in case Glide doesn't respond
        android.os.Handler timeoutHandler = new android.os.Handler();
        android.os.Handler quickFeedbackHandler = new android.os.Handler();

        // Provide quick feedback after 3 seconds if no response
        Runnable quickFeedbackRunnable = () -> {
            Log.d(TAG, "3 second mark - showing user options");

            new android.app.AlertDialog.Builder(AddLocationActivity.this)
                .setTitle("Image Loading...")
                .setMessage("The image is taking longer than expected to load. You can:\n\n" +
                           "• Wait a bit longer for the preview\n" +
                           "• Skip preview and use the URL directly\n" +
                           "• Try a different image URL\n\n" +
                           "URL: " + url)
                .setPositiveButton("Skip Preview & Use URL", (dialog, which) -> {
                    timeoutHandler.removeCallbacks(null);
                    progressBar.setVisibility(View.GONE);
                    imageUrls.add(url);
                    imageGalleryAdapter.notifyDataSetChanged();
                    layoutGalleryPlaceholder.setVisibility(View.GONE);
                    Toast.makeText(AddLocationActivity.this, "✅ Image URL saved! (Preview skipped)", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Try Different URL", (dialog, which) -> {
                    timeoutHandler.removeCallbacks(null);
                    progressBar.setVisibility(View.GONE);
                    clearSelectedImage();
                })
                .setNeutralButton("Keep Waiting", (dialog, which) -> {
                    // Just dismiss dialog and let the original timeout continue
                    Toast.makeText(AddLocationActivity.this, "Continuing to wait for image...", Toast.LENGTH_SHORT).show();
                })
                .setCancelable(false)
                .show();
        };

        Runnable timeoutRunnable = () -> {
            Log.w(TAG, "Image loading timeout reached for URL: " + url);
            progressBar.setVisibility(View.GONE);

            new android.app.AlertDialog.Builder(AddLocationActivity.this)
                .setTitle("Image Load Timeout")
                .setMessage("The image failed to load within 15 seconds. This might be due to:\n\n" +
                           "• Slow or unstable internet connection\n" +
                           "• Large image file size\n" +
                           "• Server issues with the image host\n\n" +
                           "However, you can still use this URL for your location submission.\n\n" +
                           "URL: " + url)
                .setPositiveButton("Use URL Anyway", (dialog, which) -> {
                    imageUrls.add(url);
                    layoutGalleryPlaceholder.setVisibility(View.GONE);
                    Toast.makeText(AddLocationActivity.this, "✅ Image URL saved! (Will work in final app)", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Try Different Image", (dialog, which) -> {
                    clearSelectedImage();
                })
                .setNeutralButton("Retry Loading", (dialog, which) -> {
                    loadImageWithGlide(url);
                })
                .setCancelable(false)
                .show();
        };

        // Start quick feedback timer (3 seconds)
        quickFeedbackHandler.postDelayed(quickFeedbackRunnable, 3000);

        // Start timeout timer (15 seconds)
        timeoutHandler.postDelayed(timeoutRunnable, 15000);

        try {
            Glide.with(this)
                .load(url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .skipMemoryCache(true) // Skip cache to ensure fresh load
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE) // Disable disk cache for testing
                .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(com.bumptech.glide.load.engine.GlideException e, Object model,
                        com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {

                        // Cancel all timers since we got a response
                        timeoutHandler.removeCallbacks(timeoutRunnable);
                        quickFeedbackHandler.removeCallbacks(quickFeedbackRunnable);

                        Log.e(TAG, "Glide onLoadFailed called for URL: " + url);
                        if (e != null) {
                            Log.e(TAG, "Glide error details: " + e.getMessage());
                            e.logRootCauses(TAG);
                        }

                        progressBar.setVisibility(View.GONE);

                        // Show error dialog with helpful message
                        runOnUiThread(() -> {
                            new android.app.AlertDialog.Builder(AddLocationActivity.this)
                                .setTitle("Image Load Failed")
                                .setMessage("Unable to load the image for preview. However, the URL might still work in the final app.\n\n" +
                                           "Error details:\n" + (e != null ? e.getMessage() : "Unknown error") + "\n\n" +
                                           "URL: " + url + "\n\n" +
                                           "Would you like to use this URL anyway?")
                                .setPositiveButton("Use URL Anyway", (dialog, which) -> {
                                    imageUrls.add(url);
                                    layoutGalleryPlaceholder.setVisibility(View.GONE);
                                    Toast.makeText(AddLocationActivity.this, "✅ Image URL saved! (Preview failed but URL stored)", Toast.LENGTH_LONG).show();
                                })
                                .setNegativeButton("Try Different Image", (dialog, which) -> {
                                    clearSelectedImage();
                                })
                                .setNeutralButton("Retry", (dialog, which) -> {
                                    loadImageWithGlide(url);
                                })
                                .show();
                        });

                        return false; // Allow Glide to show error drawable
                    }

                    @Override
                    public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model,
                        com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                        com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {

                        // Cancel all timers since we got a response
                        timeoutHandler.removeCallbacks(timeoutRunnable);
                        quickFeedbackHandler.removeCallbacks(quickFeedbackRunnable);

                        Log.d(TAG, "🎉 Glide onResourceReady called for URL: " + url);
                        Log.d(TAG, "DataSource: " + dataSource);

                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            layoutGalleryPlaceholder.setVisibility(View.GONE);
                            imageUrls.add(url); // Add the successfully loaded URL to the gallery
                            imageGalleryAdapter.notifyDataSetChanged();
                            updateGalleryVisibility();
                            Toast.makeText(AddLocationActivity.this, "🎉 Image loaded successfully!", Toast.LENGTH_SHORT).show();
                        });

                        return false; // Allow Glide to handle the image display
                    }
                });
                // Remove the .into(ivSelectedImage) since we're not using a single image view anymore

            Log.d(TAG, "Glide request initiated successfully");

        } catch (Exception e) {
            // Cancel timers if exception occurs
            timeoutHandler.removeCallbacks(timeoutRunnable);
            quickFeedbackHandler.removeCallbacks(quickFeedbackRunnable);

            Log.e(TAG, "Exception while setting up Glide request: " + e.getMessage());
            progressBar.setVisibility(View.GONE);

            new android.app.AlertDialog.Builder(AddLocationActivity.this)
                .setTitle("Setup Error")
                .setMessage("Error setting up image load: " + e.getMessage() + "\n\nWould you like to use the URL anyway?")
                .setPositiveButton("Use URL Anyway", (dialog, which) -> {
                    imageUrls.add(url);
                    layoutGalleryPlaceholder.setVisibility(View.GONE);
                    Toast.makeText(AddLocationActivity.this, "✅ Image URL saved!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    clearSelectedImage();
                })
                .show();
        }
    }

    private void clearSelectedImage() {
        etImageUrl.setText("");
        // Removed ivSelectedImage reference since we're using gallery now
        updateGalleryVisibility();
    }

    private void clearImageGallery() {
        imageUrls.clear();
        imageGalleryAdapter.notifyDataSetChanged();
        layoutGalleryPlaceholder.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Image gallery cleared", Toast.LENGTH_SHORT).show();
    }

    private void validateAndSubmitLocation() {
        String locationName = etLocationName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String contributorName = etContributorName.getText().toString().trim();
        String youtubeUrl = etYoutubeUrl.getText().toString().trim();
        String province = etProvince.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(locationName)) {
            etLocationName.setError("Location name is required");
            etLocationName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Description is required");
            etDescription.requestFocus();
            return;
        }

        // Updated description length check - increased to 3000 characters
        if (description.length() > 3000) {
            etDescription.setError("Description cannot exceed 3000 characters (" + description.length() + "/3000)");
            etDescription.requestFocus();
            return;
        }

        if (description.length() < 50) {
            etDescription.setError("Description must be at least 50 characters (" + description.length() + "/50)");
            etDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(category)) {
            etCategory.setError("Category is required");
            etCategory.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(city)) {
            etCity.setError("City is required");
            etCity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(contributorName)) {
            etContributorName.setError("Your name is required");
            etContributorName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(province)) {
            etProvince.setError("Province is required");
            etProvince.requestFocus();
            return;
        }

        // Check if at least one image URL is provided
        if (imageUrls.isEmpty()) {
            Toast.makeText(this, "Please add at least one image URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate each image URL
        for (String imageUrl : imageUrls) {
            if (!isValidUrl(imageUrl)) {
                Toast.makeText(this, "Invalid image URL: " + imageUrl, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Validate YouTube URL if provided
        if (!TextUtils.isEmpty(youtubeUrl) && !isValidYouTubeUrl(youtubeUrl)) {
            etYoutubeUrl.setError("Please enter a valid YouTube URL");
            etYoutubeUrl.requestFocus();
            return;
        }

        // All validations passed, submit the location
        submitLocation(locationName, description, category, city, contributorName, youtubeUrl, province, imageUrls);
    }

    private boolean isValidYouTubeUrl(String url) {
        return url.contains("youtube.com/watch?v=") || url.contains("youtu.be/");
    }

    private void submitLocation(String locationName, String description, String category,
                              String city, String contributorName, String youtubeUrl, String province, ArrayList<String> imageUrls) {
        progressBar.setVisibility(View.VISIBLE);

        saveLocationToFirestore(locationName, description, category, city,
                              contributorName, youtubeUrl, imageUrls, province);
    }

    private void saveLocationToFirestore(String locationName, String description, String category,
                                       String city, String contributorName, String youtubeUrl,
                                       ArrayList<String> imageUrls, String province) {

        // Create location data map
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("name", locationName);
        locationData.put("description", description);
        locationData.put("category", category);
        locationData.put("city", city);
        locationData.put("province", province);
        locationData.put("contributorName", contributorName);
        locationData.put("contributedAt", System.currentTimeMillis());
        locationData.put("youtubeUrl", youtubeUrl.isEmpty() ? "" : youtubeUrl);

        // Create images list with the provided URLs
        locationData.put("images", imageUrls);

        // Save to Firestore under cities/{city}/attractions collection
        firestore.collection("cities")
                .document(city)
                .collection("attractions")
                .add(locationData)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Log.d(TAG, "Location added successfully with ID: " + documentReference.getId());

                    Toast.makeText(this, "Location added successfully! Thank you for your contribution.",
                                 Toast.LENGTH_LONG).show();

                    // Clear form after successful submission
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e(TAG, "Error adding location: " + e.getMessage());

                    String errorMessage = "Failed to add location";
                    if (e.getMessage() != null) {
                        if (e.getMessage().contains("PERMISSION_DENIED")) {
                            errorMessage = "Permission denied. Please check Firestore security rules.";
                        } else if (e.getMessage().contains("Network")) {
                            errorMessage = "Network error. Please check your internet connection.";
                        } else {
                            errorMessage = "Error: " + e.getMessage();
                        }
                    }

                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                });
    }

    private void clearForm() {
        etLocationName.setText("");
        etDescription.setText("");
        etCategory.setText("");
        etCity.setText("");
        etProvince.setText("");
        etContributorName.setText("");
        etYoutubeUrl.setText("");
        etImageUrl.setText("");

        imageUrls.clear();
        imageGalleryAdapter.notifyDataSetChanged();
        layoutGalleryPlaceholder.setVisibility(View.VISIBLE);

        tilCity.setHint("City *");
    }

    private void playTutorialVideo() {
        try {
            android.content.Intent youtubeIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(TUTORIAL_VIDEO_URL));
            youtubeIntent.setPackage("com.google.android.youtube");

            // Check if YouTube app is installed
            if (youtubeIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(youtubeIntent);
                Toast.makeText(this, "Opening tutorial in YouTube app", Toast.LENGTH_SHORT).show();
            } else {
                // Fallback to web browser
                android.content.Intent webIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(TUTORIAL_VIDEO_URL));
                startActivity(webIntent);
                Toast.makeText(this, "Opening tutorial in browser", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing tutorial video: " + e.getMessage());
            Toast.makeText(this, "Unable to play tutorial video", Toast.LENGTH_SHORT).show();
        }
    }

    // Improved tutorial video setup with better instructions
    private void setupTutorialVideo() {
        // Load tutorial video thumbnail
        String videoId = extractYouTubeVideoId(TUTORIAL_VIDEO_URL);
        if (videoId != null && !videoId.equals("YOUR_TUTORIAL_VIDEO_ID")) {
            String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/maxresdefault.jpg";

            Glide.with(this)
                .load(thumbnailUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(ivTutorialThumbnail);
        } else {
            // Use a default image for tutorial placeholder
            ivTutorialThumbnail.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Setup click listeners for video elements
        ivTutorialThumbnail.setOnClickListener(v -> playTutorialVideo());
    }

    private String extractYouTubeVideoId(String youtubeUrl) {
        try {
            String videoId = null;

            if (youtubeUrl.contains("youtube.com/watch?v=")) {
                videoId = youtubeUrl.substring(youtubeUrl.indexOf("v=") + 2);
            } else if (youtubeUrl.contains("youtu.be/")) {
                videoId = youtubeUrl.substring(youtubeUrl.lastIndexOf("/") + 1);
            }

            // Remove any additional parameters
            if (videoId != null && videoId.contains("&")) {
                videoId = videoId.substring(0, videoId.indexOf("&"));
            }

            return videoId;
        } catch (Exception e) {
            Log.e(TAG, "Error extracting YouTube video ID: " + e.getMessage());
            return null;
        }
    }

    /**
     * Opens Google Photos helper dialog with instructions for sharing images
     * Since using Firebase free plan, this helps users easily get Google Photos shared links
     */
    private void openGooglePhotosHelper() {
        // Create a simple dialog with instructions for using Google Photos shared links
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("📷 Import from Google Photos")
                .setMessage("Follow these simple steps to add images from Google Photos:\n\n" +
                        "1️⃣ Open Google Photos app on your phone\n\n" +
                        "2️⃣ Select the photo you want to share\n\n" +
                        "3️⃣ Tap the Share button\n\n" +
                        "4️⃣ Choose 'Copy link' or 'Create link'\n\n" +
                        "5️⃣ Come back to this app\n\n" +
                        "6️⃣ Paste the link in the Image URL field above\n\n" +
                        "💡 Tip: Every Android phone has Google Photos by default, making this the easiest way to share your travel photos!")
                .setPositiveButton("Open Google Photos", (dialog, which) -> {
                    openGooglePhotosApp();
                })
                .setNegativeButton("Got it!", (dialog, which) -> {
                    dialog.dismiss();
                    // Focus on the image URL field
                    etImageUrl.requestFocus();
                })
                .setNeutralButton("Auto-Paste", (dialog, which) -> {
                    checkClipboardForGooglePhotosLink();
                })
                .setCancelable(true)
                .show();
    }

    /**
     * Opens the Google Photos app if available
     */
    private void openGooglePhotosApp() {
        try {
            // Try to open Google Photos app
            android.content.Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.photos");

            if (intent != null) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(this, "📷 Opening Google Photos app...\n\nSelect a photo → Share → Copy link", Toast.LENGTH_LONG).show();
            } else {
                // Fallback: Open Google Photos on web
                android.content.Intent webIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://photos.google.com"));
                startActivity(webIntent);
                Toast.makeText(this, "Opening Google Photos in browser", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening Google Photos: " + e.getMessage());
            Toast.makeText(this, "Unable to open Google Photos. Please open it manually.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks clipboard for Google Photos links and auto-pastes if found
     */
    private void checkClipboardForGooglePhotosLink() {
        try {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);

            if (clipboard != null && clipboard.hasPrimaryClip()) {
                android.content.ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                String clipText = item.getText().toString();

                // Check if clipboard contains a Google Photos link
                if (isGooglePhotosLink(clipText)) {
                    // Convert to direct image URL
                    String directUrl = convertGooglePhotosLinkToDirectUrl(clipText);
                    etImageUrl.setText(directUrl);

                    Toast.makeText(this, "✅ Google Photos link pasted!\n\nTap 'Load Image' to preview", Toast.LENGTH_LONG).show();

                    // Automatically load the image
                    loadImageFromUrl();
                } else {
                    Toast.makeText(this, "❌ No Google Photos link found in clipboard.\n\nPlease copy a photo link from Google Photos first.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "❌ Clipboard is empty.\n\nPlease copy a photo link from Google Photos first.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking clipboard: " + e.getMessage());
            Toast.makeText(this, "Unable to check clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Checks if a URL is a Google Photos shared link
     */
    private boolean isGooglePhotosLink(String url) {
        return url != null && (
                url.contains("photos.google.com") ||
                url.contains("photos.app.goo.gl") ||
                url.contains("goo.gl/photos") ||
                url.contains("lh3.googleusercontent.com")
        );
    }

    /**
     * Converts Google Photos shared link to a direct image URL
     * This method handles different Google Photos URL formats
     */
    private String convertGooglePhotosLinkToDirectUrl(String googlePhotosUrl) {
        try {
            // If it's already a direct googleusercontent link, return as is
            if (googlePhotosUrl.contains("lh3.googleusercontent.com")) {
                return googlePhotosUrl;
            }

            // For shared Google Photos links, we'll return the original URL
            // Google Photos shared links work directly in most image loading libraries like Glide
            // The link will automatically redirect to the actual image
            return googlePhotosUrl;

        } catch (Exception e) {
            Log.e(TAG, "Error converting Google Photos URL: " + e.getMessage());
            return googlePhotosUrl; // Return original URL as fallback
        }
    }

    /**
     * Enhanced URL validation that includes Google Photos links
     */
    private boolean isValidImageUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        // Check for basic URL format
        boolean isValidUrl = url.startsWith("http://") || url.startsWith("https://");

        // Additional check for common image hosting services
        boolean isImageHost = url.contains("imgur.com") ||
                             url.contains("googleusercontent.com") ||
                             url.contains("photos.google.com") ||
                             url.contains("dropbox.com") ||
                             url.contains("drive.google.com") ||
                             url.endsWith(".jpg") ||
                             url.endsWith(".jpeg") ||
                             url.endsWith(".png") ||
                             url.endsWith(".gif") ||
                             url.endsWith(".webp");

        return isValidUrl && (isImageHost || isGooglePhotosLink(url));
    }

    @Override
    public void onImageClick(int position, String imageUrl) {
        // Handle image click events from the gallery
        Log.d(TAG, "Image clicked: " + imageUrl);
        // Show full-screen image activity or any other action
        Toast.makeText(this, "Image URL: " + imageUrl, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onImageRemove(int position) {
        // Handle image removal from gallery
        if (position >= 0 && position < imageUrls.size()) {
            imageUrls.remove(position);
            imageGalleryAdapter.notifyItemRemoved(position);
            imageGalleryAdapter.notifyItemRangeChanged(position, imageUrls.size());
            updateGalleryVisibility();
            Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show();
        }
    }
}
