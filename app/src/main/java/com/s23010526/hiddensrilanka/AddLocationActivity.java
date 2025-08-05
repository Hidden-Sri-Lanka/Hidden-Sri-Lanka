package com.s23010526.hiddensrilanka;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddLocationActivity extends BaseActivity {

    private static final String TAG = "AddLocationActivity";

    // UI Components
    private EditText etLocationName, etDescription, etYoutubeUrl, etContributorName, etImageUrl;
    private AutoCompleteTextView etCategory, etCity, etProvince;
    private ImageView ivSelectedImage;
    private LinearLayout layoutImagePlaceholder;
    private MaterialButton btnSelectImage, btnSubmitLocation, btnLoadUrl;
    private TextInputLayout tilCategory, tilCity, tilProvince;

    // Firebase
    private FirebaseFirestore firestore;
    private StorageReference storageRef;

    // Data
    private Uri selectedImageUri;
    private String imageUrl; // To store either uploaded image URL or user-provided URL
    private ProgressDialog progressDialog;

    // Image picker launcher
    private ActivityResultLauncher<Intent> imagePickerLauncher;

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
        setupImagePicker();
        setupDropdowns();
        setupClickListeners();
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
        layoutImagePlaceholder = findViewById(R.id.layout_image_placeholder);
        btnSelectImage = findViewById(R.id.btn_select_image);
        btnSubmitLocation = findViewById(R.id.btn_submit_location);
        btnLoadUrl = findViewById(R.id.btn_load_url);
        tilCategory = findViewById(R.id.til_category);
        tilCity = findViewById(R.id.til_city);
        tilProvince = findViewById(R.id.til_province);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading location...");
        progressDialog.setCancelable(false);
    }

    private void initializeFirebase() {
        firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Check Firebase connection
        Log.d(TAG, "Firebase Storage initialized");
        Log.d(TAG, "Storage reference: " + storageRef.toString());

        // Test Firebase Storage connection
        testFirebaseStorageConnection();
    }

    private void testFirebaseStorageConnection() {
        // Test if we can access Firebase Storage
        StorageReference testRef = storageRef.child("test.txt");
        testRef.getDownloadUrl()
            .addOnSuccessListener(uri -> Log.d(TAG, "Firebase Storage connection successful"))
            .addOnFailureListener(e -> {
                Log.w(TAG, "Firebase Storage connection test failed: " + e.getMessage());
                if (e.getMessage() != null && e.getMessage().contains("does not exist")) {
                    Log.d(TAG, "Storage accessible but test file doesn't exist (this is normal)");
                }
            });
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

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    displaySelectedImage(selectedImageUri);
                }
            }
        );
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
        etCity.setEnabled(true); // Enable city selection immediately

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
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnSubmitLocation.setOnClickListener(v -> validateAndSubmitLocation());
        btnLoadUrl.setOnClickListener(v -> loadImageFromUrl());

        // Province selection listener - optional filtering
        etProvince.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProvince = parent.getItemAtPosition(position).toString();
            updateCityDropdown(selectedProvince);
        });
    }

    private void updateCityDropdown(String selectedProvince) {
        String[] cities = provinceCityMap.get(selectedProvince);
        if (cities != null) {
            // Update city dropdown with province-specific cities
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cities);
            etCity.setAdapter(cityAdapter);
            etCity.setText(""); // Clear any previous selection

            // Update hint to show filtered cities
            tilCity.setHint("Select City from " + selectedProvince);

            Toast.makeText(this, "Cities filtered for " + selectedProvince, Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }

    private void loadImageFromUrl() {
        String url = etImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(url)) {
            etImageUrl.setError("Please enter an image URL");
            etImageUrl.requestFocus();
            return;
        }

        if (!isValidImageUrl(url)) {
            etImageUrl.setError("Please enter a valid image URL");
            etImageUrl.requestFocus();
            return;
        }

        // Clear any selected image from gallery
        selectedImageUri = null;

        // Store the URL for later use
        imageUrl = url;

        // Load and display the image from URL
        loadImageFromUrl(url);
    }

    private boolean isValidImageUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private void loadImageFromUrl(String url) {
        progressDialog.setMessage("Loading image...");
        progressDialog.show();

        Glide.with(this)
            .load(url)
            .into(ivSelectedImage);

        // Hide placeholder and show image
        layoutImagePlaceholder.setVisibility(View.GONE);
        ivSelectedImage.setVisibility(View.VISIBLE);
        btnSelectImage.setText("Change Image");

        progressDialog.dismiss();
        Toast.makeText(this, "Image loaded successfully", Toast.LENGTH_SHORT).show();
    }

    private void displaySelectedImage(Uri imageUri) {
        ivSelectedImage.setImageURI(imageUri);
        layoutImagePlaceholder.setVisibility(View.GONE);
        ivSelectedImage.setVisibility(View.VISIBLE);
        btnSelectImage.setText("Change Image");

        // Clear any URL input since user selected from gallery
        etImageUrl.setText("");
        imageUrl = null;
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

        // Check if either image is selected or URL is provided
        String providedImageUrl = etImageUrl.getText().toString().trim();
        if (selectedImageUri == null && TextUtils.isEmpty(providedImageUrl)) {
            Toast.makeText(this, "Please select an image or provide an image URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate image URL if provided
        if (!TextUtils.isEmpty(providedImageUrl) && !isValidImageUrl(providedImageUrl)) {
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
        submitLocation(locationName, description, category, city, contributorName, youtubeUrl, province);
    }

    private boolean isValidYouTubeUrl(String url) {
        return url.contains("youtube.com/watch?v=") || url.contains("youtu.be/");
    }

    private void submitLocation(String locationName, String description, String category,
                              String city, String contributorName, String youtubeUrl, String province) {
        progressDialog.show();

        // If an image file is selected, upload it first
        if (selectedImageUri != null) {
            uploadImageToStorage(locationName, uploadedUrl -> {
                if (uploadedUrl != null) {
                    // Image uploaded successfully, now save location data
                    saveLocationToFirestore(locationName, description, category, city,
                                          contributorName, youtubeUrl, uploadedUrl, province);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to upload image. Please try again.",
                                 Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Use provided image URL
            String providedImageUrl = etImageUrl.getText().toString().trim();
            saveLocationToFirestore(locationName, description, category, city,
                                  contributorName, youtubeUrl, providedImageUrl, province);
        }
    }

    private void uploadImageToStorage(String locationName, OnImageUploadListener listener) {
        String fileName = "locations/" + UUID.randomUUID().toString() + "_" +
                         locationName.replaceAll("[^a-zA-Z0-9]", "_") + ".jpg";

        StorageReference imageRef = storageRef.child(fileName);

        Log.d(TAG, "Starting image upload to: " + fileName);
        Log.d(TAG, "Selected image URI: " + selectedImageUri.toString());

        imageRef.putFile(selectedImageUri)
            .addOnProgressListener(taskSnapshot -> {
                // Show upload progress
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload progress: " + progress + "%");
                progressDialog.setMessage("Uploading image... " + (int) progress + "%");
            })
            .addOnSuccessListener(taskSnapshot -> {
                Log.d(TAG, "Image upload successful, getting download URL...");
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "Image uploaded successfully: " + uri.toString());
                    listener.onImageUploaded(uri.toString());
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                    listener.onImageUploaded(null);
                });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to upload image: " + e.getMessage());

                // Provide more specific error messages
                String errorMessage = "Failed to upload image";
                if (e.getMessage() != null) {
                    if (e.getMessage().contains("User does not have permission")) {
                        errorMessage = "Permission denied. Please check Firebase Storage rules.";
                    } else if (e.getMessage().contains("Network")) {
                        errorMessage = "Network error. Please check your internet connection.";
                    } else if (e.getMessage().contains("storage bucket not found")) {
                        errorMessage = "Firebase Storage not properly configured.";
                    } else {
                        errorMessage = "Upload failed: " + e.getMessage();
                    }
                }

                progressDialog.dismiss();
                Toast.makeText(AddLocationActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                listener.onImageUploaded(null);
            });
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

        // Create images list
        List<String> images = new ArrayList<>();
        images.add(finalImageUrl);
        locationData.put("images", images);

        // Save to Firestore under cities/{city}/attractions collection
        firestore.collection("cities")
                .document(city)
                .collection("attractions")
                .add(locationData)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Log.d(TAG, "Location added successfully with ID: " + documentReference.getId());

                    Toast.makeText(this, "Location added successfully! Thank you for your contribution.",
                                 Toast.LENGTH_LONG).show();

                    // Clear form after successful submission
                    clearForm();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
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

        selectedImageUri = null;
        imageUrl = null;

        ivSelectedImage.setVisibility(View.GONE);
        layoutImagePlaceholder.setVisibility(View.VISIBLE);
        btnSelectImage.setText("Select Image");

        tilCity.setHint("City *");
    }

    // Interface for image upload callback
    interface OnImageUploadListener {
        void onImageUploaded(String imageUrl);
    }
}
