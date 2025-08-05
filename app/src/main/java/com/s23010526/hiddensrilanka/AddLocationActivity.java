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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

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
    private MaterialButton btnSelectImage, btnSubmitLocation, btnLoadUrl;
    private TextInputLayout tilCategory, tilCity, tilProvince;

    // Firebase
    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
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
        initializeProvinceCityMapping(); // Add this line
        setupImagePicker();
        setupDropdowns();
        setupClickListeners();
    }

    private void initializeViews() {
        etLocationName = findViewById(R.id.et_location_name);
        etDescription = findViewById(R.id.et_description);
        etYoutubeUrl = findViewById(R.id.et_youtube_url);
        etContributorName = findViewById(R.id.et_contributor_name);
        etCategory = findViewById(R.id.et_category);
        etCity = findViewById(R.id.et_city);
        etProvince = findViewById(R.id.et_province);
        ivSelectedImage = findViewById(R.id.iv_selected_image);
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
        storage = FirebaseStorage.getInstance();
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
                    ivSelectedImage.setImageURI(selectedImageUri);
                    ivSelectedImage.setVisibility(View.VISIBLE);
                    btnSelectImage.setText("Change Image");
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

        // Comprehensive Sri Lankan cities dropdown (organized by province)
        String[] cities = {
            // Western Province
            "Colombo", "Gampaha", "Kalutara", "Negombo", "Panadura", "Moratuwa",
            "Sri Jayawardenepura Kotte", "Dehiwala", "Mount Lavinia", "Kelaniya",
            "Ja-Ela", "Wattala", "Peliyagoda", "Ragama", "Kaduwela", "Maharagama",
            "Kotte", "Battaramulla", "Homagama", "Padukka", "Hanwella", "Avissawella",
            "Beruwala", "Bentota", "Aluthgama", "Wadduwa", "Payagala",

            // Central Province
            "Kandy", "Matale", "Nuwara Eliya", "Gampola", "Nawalapitiya", "Hatton",
            "Talawakelle", "Nanu Oya", "Haputale", "Bandarawela", "Ella", "Welimada",
            "Badulla", "Passara", "Mahiyanganaya", "Diyatalawa", "Pussellawa",
            "Ramboda", "Maskeliya", "Norton Bridge", "Watawala", "Dikoya", "Bogawantalawa",

            // Southern Province
            "Galle", "Matara", "Hambantota", "Tangalle", "Mirissa", "Weligama",
            "Dikwella", "Tissamaharama", "Kataragama", "Embilipitiya", "Suriyawewa",
            "Ambalantota", "Beliatta", "Deniyaya", "Akuressa", "Kamburupitiya",
            "Kirinda", "Yala", "Bundala", "Unawatuna", "Hikkaduwa", "Bentota",

            // Northern Province
            "Jaffna", "Vavuniya", "Mannar", "Kilinochchi", "Mullaittivu", "Point Pedro",
            "Chavakachcheri", "Valvettithurai", "Kayts", "Karainagar", "Nallur",
            "Kondavil", "Tellippalai", "Kopay", "Udupiddy", "Elephant Pass",

            // Eastern Province
            "Trincomalee", "Batticaloa", "Ampara", "Kalmunai", "Akkaraipattu",
            "Sammanthurai", "Kattankudy", "Eravur", "Valaichchenai", "Pasikudah",
            "Kalkudah", "Arugam Bay", "Pottuvil", "Lahugala", "Siyambalanduwa",
            "Uhana", "Maha Oya", "Nintavur", "Addalachchenai", "Chenkalady",

            // North Western Province
            "Kurunegala", "Puttalam", "Chilaw", "Wariyapola", "Mawathagama",
            "Kuliyapitiya", "Narammala", "Pannala", "Alawwa", "Bingiriya",
            "Nikaweratiya", "Giriulla", "Polgahawela", "Kegalle", "Mawanella",
            "Warakapola", "Ruwanwella", "Yatiyantota", "Deraniyagala", "Kitulgala",

            // North Central Province
            "Anuradhapura", "Polonnaruwa", "Dambulla", "Sigiriya", "Habarana",
            "Mihintale", "Kekirawa", "Tambuttegama", "Galenbindunuwewa", "Eppawala",
            "Medawachchiya", "Rambewa", "Thambuttegama", "Galnewa", "Palagala",
            "Hingurakgoda", "Minneriya", "Medirigiriya", "Welikanda", "Dimbulagala",

            // Uva Province
            "Badulla", "Monaragala", "Bandarawela", "Ella", "Haputale", "Welimada",
            "Passara", "Mahiyanganaya", "Diyatalawa", "Hali Ela", "Demodara",
            "Namunukula", "Uva Paranagama", "Kandaketiya", "Haldummulla", "Hapugastenna",
            "Soranathota", "Meegahakiula", "Buttala", "Wellawaya", "Thanamalwila",
            "Kataragama", "Siyambalanduwa", "Madulla", "Bibile",

            // Sabaragamuwa Province
            "Ratnapura", "Kegalle", "Embilipitiya", "Balangoda", "Kahawatta",
            "Pelmadulla", "Eheliyagoda", "Kuruwita", "Godakawela", "Rakwana",
            "Nivitigala", "Kalawana", "Kolonne", "Weligepola", "Ayagama",
            "Imbulpe", "Lellopitiya", "Opanayaka", "Palmadulla", "Suriyawewa"
        };

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_dropdown_item_1line, cities);
        etCity.setAdapter(cityAdapter);

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
        btnLoadUrl.setOnClickListener(v -> loadImageUrl());

        // Province selection listener - enables city dropdown when province is selected
        etProvince.setOnItemClickListener((parent, view, position, id) -> {
            String selectedProvince = parent.getItemAtPosition(position).toString();
            updateCityDropdown(selectedProvince);
        });

        // Also handle manual text entry for province
        etProvince.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String enteredProvince = etProvince.getText().toString();
                if (provinceCityMap.containsKey(enteredProvince)) {
                    updateCityDropdown(enteredProvince);
                }
            }
        });
    }

    private void updateCityDropdown(String selectedProvince) {
        String[] cities = provinceCityMap.get(selectedProvince);
        if (cities != null) {
            // Enable city dropdown and update its options
            etCity.setEnabled(true);
            etCity.setText(""); // Clear any previous selection

            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, cities);
            etCity.setAdapter(cityAdapter);

            // Update hint to show filtered cities
            tilCity.setHint("Select City from " + selectedProvince);

            Toast.makeText(this, "Cities updated for " + selectedProvince, Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Image"));
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

        if (selectedImageUri == null && TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(this, "Please select an image or provide an image URL", Toast.LENGTH_SHORT).show();
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

        // If an image is selected, upload it first
        if (selectedImageUri != null) {
            uploadImageToStorage(locationName, imageUrl -> {
                if (imageUrl != null) {
                    // Image uploaded successfully, now save location data
                    saveLocationToFirestore(locationName, description, category, city,
                                          contributorName, youtubeUrl, imageUrl, province);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Failed to upload image. Please try again.",
                                 Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // No image selected, directly save the location data with the provided image URL
            saveLocationToFirestore(locationName, description, category, city, contributorName, youtubeUrl, this.imageUrl, province);
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
                    Log.e(TAG, "Download URL error details: ", e);
                    listener.onImageUploaded(null);
                });
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Failed to upload image: " + e.getMessage());
                Log.e(TAG, "Upload error details: ", e);

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
                                       String imageUrl, String province) {

        // Create location data map
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("name", locationName);
        locationData.put("description", description);
        locationData.put("category", category);
        locationData.put("contributorName", contributorName);
        locationData.put("contributedAt", System.currentTimeMillis());
        locationData.put("youtubeUrl", youtubeUrl.isEmpty() ? "" : youtubeUrl);

        // Create images list
        List<String> images = new ArrayList<>();
        images.add(imageUrl);
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

                    // Clear form and go back
                    clearForm();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Error adding location: " + e.getMessage());
                    Toast.makeText(this, "Failed to add location: " + e.getMessage(),
                                 Toast.LENGTH_SHORT).show();
                });
    }

    private void clearForm() {
        etLocationName.setText("");
        etDescription.setText("");
        etCategory.setText("");
        etCity.setText("");
        etContributorName.setText("");
        etYoutubeUrl.setText("");
        etImageUrl.setText("");
        ivSelectedImage.setImageDrawable(null);
        ivSelectedImage.setVisibility(View.GONE);
        btnSelectImage.setText("Select Image");
        selectedImageUri = null;
        imageUrl = null;
    }

    private void loadImageUrl() {
        String url = etImageUrl.getText().toString().trim();
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "Please enter an image URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate URL format (basic validation)
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            etImageUrl.setError("Invalid URL. Please enter a valid image URL");
            etImageUrl.requestFocus();
            return;
        }

        // Load image from URL (using Glide or any other image loading library)
        // For now, just setting the URL to the imageUrl variable
        imageUrl = url;
        Toast.makeText(this, "Image URL loaded. You can now submit the location.", Toast.LENGTH_SHORT).show();
    }

    // Interface for image upload callback
    private interface OnImageUploadListener {
        void onImageUploaded(String imageUrl);
    }
}
