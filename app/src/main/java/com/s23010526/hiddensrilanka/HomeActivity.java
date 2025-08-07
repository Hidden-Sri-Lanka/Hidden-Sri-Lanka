package com.s23010526.hiddensrilanka;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

    // Add flag to prevent multiple location requests
    private boolean isLocationRequestInProgress = false;
    private boolean hasInitialLocationLoad = false;

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

        checkLocationPermission();
        setupFilterListener();
        setupRefreshButton(); // Add refresh functionality
    }

    // Add refresh button functionality
    private void setupRefreshButton() {
        // Make the search functionality work for manual city override
        findViewById(R.id.search_icon).setOnClickListener(v -> {
            EditText searchField = findViewById(R.id.toolbar_search_field);
            String searchQuery = searchField.getText().toString().trim();

            if (!searchQuery.isEmpty()) {
                // Manual city override - search for attractions in specified city
                currentCity = searchQuery;
                Toast.makeText(this, "🔍 Searching attractions in: " + searchQuery, Toast.LENGTH_SHORT).show();
                loadAttractionsFromFirestore(searchQuery, "All");

                // Clear search field and hide keyboard
                searchField.setText("");
                searchField.clearFocus();
                android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
            } else {
                // Regular refresh functionality
                Toast.makeText(this, "🔄 Refreshing location...", Toast.LENGTH_SHORT).show();
                refreshLocation();
            }
        });
    }

    // Method to refresh location and attractions
    private void refreshLocation() {
        if (isLocationRequestInProgress) {
            Log.d(TAG, "Location request already in progress, skipping refresh");
            Toast.makeText(this, "Location request already in progress...", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear current data
        currentCity = null;
        attractionList.clear();
        notifyDataSetChanged();
        hasInitialLocationLoad = false; // Reset flag for manual refresh

        // Get fresh location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentCity();
        } else {
            Toast.makeText(this, "Location permission needed for refresh", Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to update adapter efficiently
    private void notifyDataSetChanged() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Only refresh location if we haven't done initial load and no request is in progress
        if (!hasInitialLocationLoad && !isLocationRequestInProgress && currentCity == null) {
            Log.d(TAG, "onResume: Performing initial location load");
            refreshLocation();
        } else {
            Log.d(TAG, "onResume: Skipping location refresh - already loaded or in progress");
        }
    }

    // Location Handling Methods
    private void checkLocationPermission() {
        if (isLocationRequestInProgress) {
            Log.d(TAG, "Location request already in progress, skipping");
            return;
        }

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
        if (isLocationRequestInProgress) {
            Log.d(TAG, "Location request already in progress, skipping getCurrentCity");
            return;
        }

        isLocationRequestInProgress = true;
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "Starting location detection...");

        // Clear any cached location first
        fusedLocationProviderClient.flushLocations();

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    isLocationRequestInProgress = false;
                    hasInitialLocationLoad = true;

                    if (location != null) {
                        Log.d(TAG, "Location found: " + location.getLatitude() + ", " + location.getLongitude());

                        // Always try to geocode the coordinates to get city name
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

                                Log.d(TAG, "Geocoded city: " + city);
                                Log.d(TAG, "Full geocoded address: " + address);

                                if (city != null) {
                                    currentCity = normalizeCityName(city);

                                    // Check if location is in Sri Lanka
                                    if (isInSriLanka(location.getLatitude(), location.getLongitude())) {
                                        Toast.makeText(this, "📍 Detected location: " + currentCity, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, "🌍 Foreign location detected: " + currentCity +
                                                     "\n🔄 Tap refresh to try again or test with Sri Lankan cities", Toast.LENGTH_LONG).show();
                                    }

                                    // Always check Firebase for attractions in the detected city
                                    loadAttractionsFromFirestore(currentCity, "All");
                                    return;
                                } else {
                                    Log.w(TAG, "Could not determine city name from geocoding");
                                    showLocationFallbackOptions();
                                }
                            } else {
                                Log.w(TAG, "No addresses found for location");
                                showLocationFallbackOptions();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Geocoder error: " + e.getMessage());
                            showLocationFallbackOptions();
                        }
                    } else {
                        Log.w(TAG, "Location is null");
                        showLocationFallbackOptions();
                    }
                })
                .addOnFailureListener(this, e -> {
                    isLocationRequestInProgress = false;
                    hasInitialLocationLoad = true;
                    Log.e(TAG, "Location error: " + e.getMessage());
                    showLocationFallbackOptions();
                });
    }

    // New method to show fallback options when location detection fails
    private void showLocationFallbackOptions() {
        isLocationRequestInProgress = false;
        hasInitialLocationLoad = true;
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "📍 Location not detected. Showing default attractions.\n🔄 Tap refresh icon to try again", Toast.LENGTH_LONG).show();

        // Try some Sri Lankan cities as fallback
        String[] sriLankanCities = {"Colombo", "Kandy", "Galle", "Anuradhapura", "Nuwara Eliya", "Kahawatta"};

        for (String city : sriLankanCities) {
            currentCity = city;
            loadAttractionsFromFirestore(city, "All");
            break; // Try first available city
        }
    }

    // Show message when location cannot be detected
    private void showNoLocationMessage() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Unable to detect your location. Please check GPS settings.", Toast.LENGTH_LONG).show();

        // Clear the attractions list and show message
        attractionList.clear();
        adapter.notifyDataSetChanged();
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

        // Only apply category filter if it's not "All"
        if (!"All".equalsIgnoreCase(category)) {
            query = query.whereEqualTo("category", category);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Query successful for " + cityName + ". Found " + task.getResult().size() + " documents.");

                if (!task.getResult().isEmpty()) {
                    // Clear previous results and add new ones
                    attractionList.clear();

                    // Found attractions for this city - show them
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Attraction attraction = document.toObject(Attraction.class);
                        attraction.setDocumentId(document.getId());
                        attractionList.add(attraction);
                        Log.d(TAG, "Added attraction: " + attraction.getName());
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                    String filterText = "All".equalsIgnoreCase(category) ? "all" : category;
                    Toast.makeText(this, "Found " + attractionList.size() + " " + filterText + " attractions in " + cityName, Toast.LENGTH_SHORT).show();
                } else {
                    // No attractions found for this city - show community growth entry
                    Log.d(TAG, "No attractions found for " + cityName + ", showing community growth entry");
                    showPlaceholderEntry(cityName);
                }
            } else {
                Exception exception = task.getException();
                Log.e(TAG, "Error getting documents from " + cityName + ": ", exception);

                progressBar.setVisibility(View.GONE);
                if (exception != null && exception.getMessage() != null && exception.getMessage().contains("PERMISSION_DENIED")) {
                    Toast.makeText(this, "⚠️ Firebase Database Access Denied!\nPlease update Firebase Security Rules.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error loading data. Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Show message when no attractions found for the detected city
    private void showNoAttractionsMessage(String cityName) {
        progressBar.setVisibility(View.GONE);
        attractionList.clear();
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "No attractions found in " + cityName + ". Be the first to add some!", Toast.LENGTH_LONG).show();
    }

    // Helper method to check if coordinates are in Sri Lanka
    private boolean isInSriLanka(double lat, double lng) {
        // Sri Lanka boundaries (approximate)
        return lat >= 5.9 && lat <= 9.9 && lng >= 79.5 && lng <= 81.9;
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

    // Add this method to your HomeActivity class
    private void seedSampleData() {
        DataSeeder seeder = new DataSeeder();
        seeder.seedSampleAttractions();
        Toast.makeText(this, "Sample attractions added!", Toast.LENGTH_SHORT).show();
    }

    // Method to show placeholder entry when no attractions found
    private void showPlaceholderEntry(String cityName) {
        progressBar.setVisibility(View.GONE);

        // Create placeholder attraction
        Attraction placeholder = new Attraction();
        placeholder.setDocumentId("placeholder_" + cityName);
        placeholder.setName("Help Us Grow Our Database! 🌟");
        placeholder.setCategory("Community Contribution");
        placeholder.setDescription("Unfortunately, we haven't updated our database with attractions from " + cityName + " yet. " +
                "Please consider adding interesting locations near your area to help other travelers discover amazing places!");
        placeholder.setYoutubeUrl("");

        // Set placeholder image - let the adapter handle the icon
        List<String> placeholderImages = new ArrayList<>();
        // Don't add any image URL - the adapter will show the special icon
        placeholder.setImages(placeholderImages);

        placeholder.setContributorName("Hidden Sri Lanka Team");
        placeholder.setContributedAt(System.currentTimeMillis());
        placeholder.setPlaceholder(true); // Mark as placeholder

        // Add to attraction list
        attractionList.clear();
        attractionList.add(placeholder);
        adapter.notifyDataSetChanged();

        Log.d(TAG, "Placeholder entry added for " + cityName);
        Toast.makeText(this, "No attractions found for " + cityName + ". Help us by adding some!", Toast.LENGTH_LONG).show();
    }

    // Call this method once to populate your database (you can trigger it with a button or on first run)
}
