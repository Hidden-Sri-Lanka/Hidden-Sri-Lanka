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

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddLocationActivity extends BaseActivity {

    private static final String TAG = "AddLocationActivity";

    // UI Components
    private EditText etLocationName, etDescription, etYoutubeUrl, etContributorName, etImageUrl;
    private AutoCompleteTextView etCategory, etCity, etProvince;
    private ImageView ivSelectedImage, ivTutorialThumbnail, btnPlayTutorial;
    private LinearLayout layoutImagePlaceholder;
    private MaterialButton btnSubmitLocation, btnLoadUrl;
    private TextInputLayout tilCity;
    private ProgressBar progressBar; // Replace ProgressDialog with ProgressBar

    // Tutorial video URL - Replace with your actual instructional video
    private static final String TUTORIAL_VIDEO_URL = "https://www.youtube.com/watch?v=YOUR_TUTORIAL_VIDEO_ID"; // Replace with actual tutorial video

    // Firebase
    private FirebaseFirestore firestore;

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
        ivSelectedImage = findViewById(R.id.iv_selected_image);
        ivTutorialThumbnail = findViewById(R.id.iv_tutorial_thumbnail);
        btnPlayTutorial = findViewById(R.id.btn_play_tutorial);
        layoutImagePlaceholder = findViewById(R.id.layout_image_placeholder);
        btnSubmitLocation = findViewById(R.id.btn_submit_location);
        btnLoadUrl = findViewById(R.id.btn_load_url);
        tilCity = findViewById(R.id.til_city);

        // Reference ProgressBar from layout
        progressBar = findViewById(R.id.progress_bar);
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

    private void loadImageFromUrl() {
        String url = etImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(url)) {
            etImageUrl.setError("Please enter an image URL");
            etImageUrl.requestFocus();
            return;
        }

        if (!isValidUrl(url)) {
            etImageUrl.setError("Please enter a valid image URL");
            etImageUrl.requestFocus();
            return;
        }

        loadAndDisplayImage(url);
    }

    private boolean isValidUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void loadAndDisplayImage(String url) {
        progressBar.setVisibility(View.VISIBLE);

        Glide.with(this)
            .load(url)
            .into(ivSelectedImage);

        layoutImagePlaceholder.setVisibility(View.GONE);
        ivSelectedImage.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Image loaded successfully", Toast.LENGTH_SHORT).show();
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

        // Check if image URL is provided
        String providedImageUrl = etImageUrl.getText().toString().trim();
        if (TextUtils.isEmpty(providedImageUrl)) {
            etImageUrl.setError("Please provide an image URL");
            etImageUrl.requestFocus();
            return;
        }

        // Validate image URL
        if (!isValidUrl(providedImageUrl)) {
            etImageUrl.setError("Please enter a valid image URL");
            etImageUrl.requestFocus();
            return;
        }

        // Validate YouTube URL if provided
        if (!TextUtils.isEmpty(youtubeUrl) && !isValidYouTubeUrl(youtubeUrl)) {
            etYoutubeUrl.setError("Please enter a valid YouTube URL");
            etYoutubeUrl.requestFocus();
            return;
        }

        // All validations passed, submit the location
        submitLocation(locationName, description, category, city, contributorName, youtubeUrl, province, providedImageUrl);
    }

    private boolean isValidYouTubeUrl(String url) {
        return url.contains("youtube.com/watch?v=") || url.contains("youtu.be/");
    }

    private void submitLocation(String locationName, String description, String category,
                              String city, String contributorName, String youtubeUrl, String province, String imageUrl) {
        progressBar.setVisibility(View.VISIBLE);

        saveLocationToFirestore(locationName, description, category, city,
                              contributorName, youtubeUrl, imageUrl, province);
    }

    private void saveLocationToFirestore(String locationName, String description, String category,
                                       String city, String contributorName, String youtubeUrl,
                                       String finalImageUrl, String province) {

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

        // Create images list with the provided URL
        List<String> images = new ArrayList<>();
        images.add(finalImageUrl);
        locationData.put("images", images);

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

        ivSelectedImage.setVisibility(View.GONE);
        layoutImagePlaceholder.setVisibility(View.VISIBLE);

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
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(ivTutorialThumbnail);
        } else {
            // Use a default image for tutorial placeholder
            ivTutorialThumbnail.setImageResource(R.drawable.ic_image_placeholder);
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
}
