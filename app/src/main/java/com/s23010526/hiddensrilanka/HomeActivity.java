package com.s23010526.hiddensrilanka;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends BaseActivity {

    // ---Declare all the variables we will need ---
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "HomeActivity";

    private AttractionAdapter adapter;
    private List<Attraction> attractionList;

    private ProgressBar progressBar;
    private ChipGroup chipGroup;

    private FirebaseFirestore firestoreDb;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String currentCity = null;

    // Fallback cities for testing and better user experience
    private final List<String> fallbackCities = Arrays.asList(
        "Colombo", "Kandy", "Galle", "Godakawela", "Anuradhapura", "Polonnaruwa",
        "Sigiriya", "Nuwara Eliya", "Ella", "Bentota", "Negombo"
    );

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.nav_home_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initialize everything
        firestoreDb = FirebaseFirestore.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        progressBar = findViewById(R.id.progressBar);
        chipGroup = findViewById(R.id.chip_group_filters);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_attractions);

        attractionList = new ArrayList<>();
        adapter = new AttractionAdapter(attractionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // FOR TESTING: Check if we should use manual test location
        checkForTestMode();

        checkLocationPermission();
        setupFilterListener();
    }

    // Location Handling Methods
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentCity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentCity();
            } else {
                Toast.makeText(this, "Location permission is required to show nearby attractions. Loading default attractions...", Toast.LENGTH_LONG).show();
                // Load default attractions when permission denied
                loadDefaultAttractions();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentCity() {
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Starting location detection...");

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Log.d(TAG, "Location found: " + location.getLatitude() + ", " + location.getLongitude());

                        // For emulator testing - check if it's a common emulator location
                        if (isEmulatorLocation(location.getLatitude(), location.getLongitude())) {
                            Log.d(TAG, "Detected emulator location, using test data");
                            Toast.makeText(this, "Using test location data for emulator", Toast.LENGTH_SHORT).show();
                            currentCity = "Colombo"; // Default for emulator
                            loadAttractionsFromFirestore(currentCity, "All");
                            return;
                        }

                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);

                                // Try multiple ways to get city name
                                String city = address.getLocality();
                                if (city == null) city = address.getSubAdminArea();
                                if (city == null) city = address.getAdminArea();
                                if (city == null) city = address.getSubLocality();

                                Log.d(TAG, "Detected city: " + city);
                                Log.d(TAG, "Full address: " + address.toString());

                                if (city != null) {
                                    currentCity = normalizeCityName(city);
                                    loadAttractionsFromFirestore(currentCity, "All");
                                } else {
                                    Log.w(TAG, "Could not determine city name from location");
                                    loadDefaultAttractions();
                                }
                            } else {
                                Log.w(TAG, "No addresses found for location");
                                loadDefaultAttractions();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Geocoder error: " + e.getMessage());
                            loadDefaultAttractions();
                        }
                    } else {
                        Log.w(TAG, "Location is null");
                        Toast.makeText(this, "Could not get location. Loading default attractions...", Toast.LENGTH_LONG).show();
                        loadDefaultAttractions();
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Location error: " + e.getMessage());
                    Toast.makeText(this, "Location error: " + e.getMessage() + ". Loading default attractions...", Toast.LENGTH_LONG).show();
                    loadDefaultAttractions();
                });
    }

    // Helper method to detect common emulator locations
    private boolean isEmulatorLocation(double lat, double lng) {
        // Common emulator default locations
        return (Math.abs(lat - 37.4219983) < 0.001 && Math.abs(lng - (-122.084)) < 0.001) || // Google HQ
               (Math.abs(lat - 0.0) < 0.001 && Math.abs(lng - 0.0) < 0.001); // Null Island
    }

    // Helper method to normalize city names for better Firebase matching
    private String normalizeCityName(String cityName) {
        if (cityName == null) return null;

        String normalized = cityName.trim();

        // Handle common Sri Lankan city name variations
        switch (normalized.toLowerCase()) {
            case "colombo municipal council":
            case "colombo district":
                return "Colombo";
            case "kandy municipal council":
            case "kandy district":
                return "Kandy";
            case "galle municipal council":
            case "galle district":
                return "Galle";
            case "anuradhapura district":
                return "Anuradhapura";
            case "polonnaruwa district":
                return "Polonnaruwa";
            case "nuwara eliya district":
                return "Nuwara Eliya";
            case "matale district":
                return "Matale";
            case "kurunegala district":
                return "Kurunegala";
            default:
                return normalized;
        }
    }

    // New method to load default attractions when location fails
    private void loadDefaultAttractions() {
        Log.d(TAG, "Loading default attractions from fallback cities");
        currentCity = "Colombo"; // Set default city
        loadAttractionsFromFirestore(currentCity, "All");
    }

    // Improved Firestore loading with better error handling and fallback
    private void loadAttractionsFromFirestore(String cityName, String category) {
        progressBar.setVisibility(View.VISIBLE);
        attractionList.clear();

        String formattedCityName = cityName.trim();
        Log.d(TAG, "Querying for city: '" + formattedCityName + "' and category: '" + category + "'");

        // Try exact city match first
        tryLoadFromCity(formattedCityName, category, 0);
    }

    private void tryLoadFromCity(String cityName, String category, int fallbackIndex) {
        Query query = firestoreDb.collection("cities").document(cityName).collection("attractions");

        if (!"All".equalsIgnoreCase(category)) {
            query = query.whereEqualTo("category", category);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Query successful for " + cityName + ". Found " + task.getResult().size() + " documents.");

                if (!task.getResult().isEmpty()) {
                    // Found attractions for this city
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Attraction attraction = document.toObject(Attraction.class);
                        attraction.setDocumentId(document.getId());
                        attractionList.add(attraction);
                        Log.d(TAG, "Added attraction: " + attraction.getName());
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    if (!cityName.equals(currentCity)) {
                        Toast.makeText(this, "Showing attractions from " + cityName, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // No attractions found, try fallback cities
                    if (fallbackIndex < fallbackCities.size()) {
                        String fallbackCity = fallbackCities.get(fallbackIndex);
                        Log.d(TAG, "No attractions found for " + cityName + ", trying fallback: " + fallbackCity);
                        tryLoadFromCity(fallbackCity, category, fallbackIndex + 1);
                    } else {
                        // No attractions found in any city
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "No attractions found. Please check your Firebase database setup.", Toast.LENGTH_LONG).show();
                        Log.w(TAG, "No attractions found in any fallback cities - check Firebase database structure");
                    }
                }
            } else {
                Exception exception = task.getException();
                Log.e(TAG, "Error getting documents from " + cityName + ": ", exception);

                // Check for specific Firebase permission errors
                if (exception != null && exception.getMessage() != null) {
                    if (exception.getMessage().contains("PERMISSION_DENIED")) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "⚠️ Firebase Database Access Denied!\n\nPlease update Firebase Security Rules to allow read access.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "🚨 CRITICAL: Firebase Firestore Security Rules are blocking data access. Update rules in Firebase Console.");
                        return; // Don't try fallback cities if it's a permission issue
                    }
                }

                // Try fallback cities on other errors
                if (fallbackIndex < fallbackCities.size()) {
                    String fallbackCity = fallbackCities.get(fallbackIndex);
                    Log.d(TAG, "Error with " + cityName + ", trying fallback: " + fallbackCity);
                    tryLoadFromCity(fallbackCity, category, fallbackIndex + 1);
                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading data. Please check your internet connection and Firebase setup.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupFilterListener() {
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                return;
            }
            Chip selectedChip = findViewById(checkedId);
            if (selectedChip != null) {
                String selectedCategory = selectedChip.getText().toString();
                String cityToQuery = currentCity != null ? currentCity : "Colombo";
                loadAttractionsFromFirestore(cityToQuery, selectedCategory);
            }
        });
    }

    // Test method to verify Firebase connection and data structure
    private void checkForTestMode() {
        // For testing purposes - you can uncomment this to force test a specific city
        // testFirebaseConnection("Colombo");
    }

    private void testFirebaseConnection(String testCity) {
        Log.d(TAG, "Testing Firebase connection with city: " + testCity);
        Toast.makeText(this, "Testing Firebase with: " + testCity, Toast.LENGTH_SHORT).show();

        currentCity = testCity;
        loadAttractionsFromFirestore(testCity, "All");
    }

    // Add this method to your HomeActivity class
    private void seedSampleData() {
        DataSeeder seeder = new DataSeeder();
        seeder.seedSampleAttractions();
        Toast.makeText(this, "Sample attractions added!", Toast.LENGTH_SHORT).show();
    }

    // Call this method once to populate your database (you can trigger it with a button or on first run)
}
