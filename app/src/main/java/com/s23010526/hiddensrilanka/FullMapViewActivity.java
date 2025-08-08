package com.s23010526.hiddensrilanka;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * FullMapViewActivity - Google Maps implementation for exploring Sri Lankan attractions
 *
 * This activity provides a comprehensive map view of all attractions with features:
 * - Interactive Google Maps with custom markers
 * - Real-time location tracking
 * - Search and filter functionality
 * - Bottom sheet with attraction details
 * - Navigation integration
 * - Different map types (Normal, Satellite, Terrain)
 * - Clustering for better performance with many markers
 *
 * @author Hidden Sri Lanka Development Team
 * @version 2.0.0
 */
public class FullMapViewActivity extends BaseActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "FullMapViewActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    // Sri Lanka default coordinates (Colombo)
    private static final LatLng DEFAULT_LOCATION = new LatLng(6.9271, 79.8612);
    private static final float DEFAULT_ZOOM = 8f;
    private static final float DETAILED_ZOOM = 15f;

    // UI Components
    private GoogleMap mMap;
    private CircularProgressIndicator loadingIndicator;
    private EditText searchEditText;
    private ImageView filterIcon;
    private FloatingActionButton myLocationFab, mapTypeFab, clusterToggleFab;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

    // Bottom sheet components
    private ImageView attractionImage, navigateButton;
    private TextView attractionName, attractionCategory, attractionDistance;

    // Data and functionality
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore firestore;
    private List<Attraction> allAttractions;
    private List<Attraction> filteredAttractions;
    private Map<Marker, Attraction> markerAttractionMap;
    private Location currentLocation;
    private int currentMapType = GoogleMap.MAP_TYPE_NORMAL;
    private boolean clusteringEnabled = true;
    private String currentSearchQuery = "";
    private Attraction selectedAttraction;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_full_map_view;
    }

    @Override
    protected String getActivityTitle() {
        return getString(R.string.nav_explore_on_map_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeComponents();
        setupMapFragment();
        setupUIListeners();
        requestLocationPermission();
        loadAttractionsFromFirestore();
    }

    /**
     * Initialize all UI components and services
     */
    private void initializeComponents() {
        // Initialize Firebase and location services
        firestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize data structures
        allAttractions = new ArrayList<>();
        filteredAttractions = new ArrayList<>();
        markerAttractionMap = new HashMap<>();

        // Initialize UI components
        loadingIndicator = findViewById(R.id.loadingIndicator);
        searchEditText = findViewById(R.id.searchEditText);
        filterIcon = findViewById(R.id.filterIcon);
        myLocationFab = findViewById(R.id.myLocationFab);
        mapTypeFab = findViewById(R.id.mapTypeFab);
        clusterToggleFab = findViewById(R.id.clusterToggleFab);
        bottomSheet = findViewById(R.id.bottomSheet);

        // Bottom sheet components
        attractionImage = findViewById(R.id.attractionImage);
        attractionName = findViewById(R.id.attractionName);
        attractionCategory = findViewById(R.id.attractionCategory);
        attractionDistance = findViewById(R.id.attractionDistance);
        navigateButton = findViewById(R.id.navigateButton);

        // Setup bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    /**
     * Setup the Google Maps fragment
     */
    private void setupMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Setup all UI event listeners
     */
    private void setupUIListeners() {
        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                filterAndDisplayAttractions();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter button
        filterIcon.setOnClickListener(v -> showFilterDialog());

        // My location button
        myLocationFab.setOnClickListener(v -> moveToCurrentLocation());

        // Map type toggle
        mapTypeFab.setOnClickListener(v -> toggleMapType());

        // Clustering toggle
        clusterToggleFab.setOnClickListener(v -> toggleClustering());

        // Navigation button in bottom sheet
        navigateButton.setOnClickListener(v -> openNavigation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Configure map settings
        setupMapSettings();

        // Set map listeners
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(latLng -> hideBottomSheet());

        // Move camera to Sri Lanka
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));

        // Get current location and display attractions
        getCurrentLocation();
        displayAttractionsOnMap();

        hideLoadingIndicator();
    }

    /**
     * Configure Google Maps settings and UI
     */
    private void setupMapSettings() {
        // Enable map controls
        mMap.getUiSettings().setZoomControlsEnabled(false); // We have custom FABs
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Enable location if permission granted
        enableMyLocationIfPermitted();
    }

    /**
     * Load attractions from Firestore database
     */
    private void loadAttractionsFromFirestore() {
        showLoadingIndicator();

        firestore.collection("attractions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        allAttractions.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Attraction attraction = document.toObject(Attraction.class);
                                if (attraction != null && isValidAttraction(attraction)) {
                                    allAttractions.add(attraction);
                                }
                            } catch (Exception e) {
                                Log.w(TAG, "Error parsing attraction: " + e.getMessage());
                            }
                        }

                        Log.d(TAG, "Loaded " + allAttractions.size() + " attractions from Firestore");
                        filterAndDisplayAttractions();

                    } else {
                        Log.w(TAG, "Error getting attractions", task.getException());
                        Toast.makeText(this, "Failed to load attractions", Toast.LENGTH_SHORT).show();
                    }
                    hideLoadingIndicator();
                });
    }

    /**
     * Validate attraction data before displaying
     */
    private boolean isValidAttraction(Attraction attraction) {
        return attraction.getName() != null && !attraction.getName().trim().isEmpty() &&
               attraction.getLatitude() != 0.0 && attraction.getLongitude() != 0.0 &&
               !attraction.isPlaceholder();
    }

    /**
     * Filter attractions based on search query and display on map
     */
    private void filterAndDisplayAttractions() {
        filteredAttractions.clear();

        if (currentSearchQuery.isEmpty()) {
            filteredAttractions.addAll(allAttractions);
        } else {
            String query = currentSearchQuery.toLowerCase();
            for (Attraction attraction : allAttractions) {
                if (matchesSearchQuery(attraction, query)) {
                    filteredAttractions.add(attraction);
                }
            }
        }

        displayAttractionsOnMap();
        Log.d(TAG, "Filtered to " + filteredAttractions.size() + " attractions");
    }

    /**
     * Check if attraction matches search query
     */
    private boolean matchesSearchQuery(Attraction attraction, String query) {
        return attraction.getName().toLowerCase().contains(query) ||
               (attraction.getCategory() != null && attraction.getCategory().toLowerCase().contains(query)) ||
               (attraction.getCity() != null && attraction.getCity().toLowerCase().contains(query)) ||
               (attraction.getDescription() != null && attraction.getDescription().toLowerCase().contains(query));
    }

    /**
     * Display filtered attractions as markers on the map
     */
    private void displayAttractionsOnMap() {
        if (mMap == null) return;

        // Clear existing markers
        mMap.clear();
        markerAttractionMap.clear();

        // Add markers for filtered attractions
        for (Attraction attraction : filteredAttractions) {
            addAttractionMarker(attraction);
        }

        Log.d(TAG, "Displayed " + filteredAttractions.size() + " markers on map");
    }

    /**
     * Add a marker for an attraction on the map
     */
    private void addAttractionMarker(Attraction attraction) {
        LatLng position = new LatLng(attraction.getLatitude(), attraction.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(attraction.getName())
                .snippet(attraction.getCategory())
                .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(attraction.getCategory())));

        Marker marker = mMap.addMarker(markerOptions);
        if (marker != null) {
            markerAttractionMap.put(marker, attraction);
        }
    }

    /**
     * Get marker color based on attraction category
     */
    private float getMarkerColor(String category) {
        if (category == null) return BitmapDescriptorFactory.HUE_RED;

        switch (category.toLowerCase()) {
            case "temple": case "religious": return BitmapDescriptorFactory.HUE_ORANGE;
            case "beach": case "coastal": return BitmapDescriptorFactory.HUE_AZURE;
            case "mountain": case "hiking": return BitmapDescriptorFactory.HUE_GREEN;
            case "waterfall": return BitmapDescriptorFactory.HUE_CYAN;
            case "historical": case "archaeological": return BitmapDescriptorFactory.HUE_VIOLET;
            case "wildlife": case "national park": return BitmapDescriptorFactory.HUE_YELLOW;
            default: return BitmapDescriptorFactory.HUE_RED;
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        Attraction attraction = markerAttractionMap.get(marker);
        if (attraction != null) {
            selectedAttraction = attraction;
            showAttractionDetails(attraction);

            // Move camera to marker
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), DETAILED_ZOOM));
            return true;
        }
        return false;
    }

    /**
     * Show attraction details in bottom sheet
     */
    private void showAttractionDetails(Attraction attraction) {
        // Set attraction data
        attractionName.setText(attraction.getName());
        attractionCategory.setText(attraction.getCategory());

        // Load attraction image
        if (attraction.getImages() != null && !attraction.getImages().isEmpty()) {
            String imageUrl = attraction.getImages().get(0);
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(attractionImage);
        } else {
            attractionImage.setImageResource(R.drawable.ic_image_placeholder);
        }

        // Calculate and display distance
        if (currentLocation != null) {
            float distance = calculateDistance(
                    currentLocation.getLatitude(), currentLocation.getLongitude(),
                    attraction.getLatitude(), attraction.getLongitude()
            );
            attractionDistance.setText(String.format(Locale.getDefault(), "%.1f km away", distance));
        } else {
            attractionDistance.setText(getString(R.string.distance_unknown));
        }

        // Show bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // Set click listener for full details
        bottomSheet.setOnClickListener(v -> openAttractionDetails(attraction));
    }

    /**
     * Calculate distance between two points in kilometers
     */
    private float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0] / 1000; // Convert to kilometers
    }

    /**
     * Hide the bottom sheet
     */
    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        selectedAttraction = null;
    }

    /**
     * Open full attraction details activity
     */
    private void openAttractionDetails(Attraction attraction) {
        Intent intent = new Intent(this, LocationDetailActivity.class);

        // Pass all attraction data using standardized keys
        intent.putExtra("attraction_name", attraction.getName());
        intent.putExtra("attraction_city", attraction.getCity());
        intent.putExtra("attraction_category", attraction.getCategory());
        intent.putExtra("attraction_description", attraction.getDescription());
        intent.putExtra("contributor_name", attraction.getContributorName());
        intent.putExtra("youtube_url", attraction.getYoutubeUrl());
        intent.putExtra("attraction_latitude", attraction.getLatitude());
        intent.putExtra("attraction_longitude", attraction.getLongitude());

        if (attraction.getImages() != null && !attraction.getImages().isEmpty()) {
            intent.putExtra("attraction_image_url", attraction.getImages().get(0));
            ArrayList<String> imagesList = new ArrayList<>(attraction.getImages());
            intent.putStringArrayListExtra("attraction_images", imagesList);
        }

        startActivity(intent);
    }

    /**
     * Open navigation to selected attraction
     */
    private void openNavigation() {
        if (selectedAttraction == null) return;

        // Create Google Maps navigation intent
        Uri gmmIntentUri = Uri.parse(String.format(Locale.getDefault(),
                "google.navigation:q=%.6f,%.6f",
                selectedAttraction.getLatitude(),
                selectedAttraction.getLongitude()));

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Fallback to browser-based maps
            Uri browserUri = Uri.parse(String.format(Locale.getDefault(),
                    "https://www.google.com/maps/dir/?api=1&destination=%.6f,%.6f",
                    selectedAttraction.getLatitude(),
                    selectedAttraction.getLongitude()));

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            startActivity(browserIntent);
        }
    }

    /**
     * Request location permission from user
     */
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Enable my location on map if permission is granted
     */
    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false); // We use custom FAB
            }
        }
    }

    /**
     * Get current user location
     */
    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLocation = location;
                            Log.d(TAG, "Current location: " + location.getLatitude() + ", " + location.getLongitude());
                        }
                    })
                    .addOnFailureListener(e -> Log.w(TAG, "Failed to get location", e));
        }
    }

    /**
     * Move camera to current location
     */
    private void moveToCurrentLocation() {
        if (currentLocation != null && mMap != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DETAILED_ZOOM));
        } else {
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
            getCurrentLocation(); // Try to get location again
        }
    }

    /**
     * Toggle between different map types
     */
    private void toggleMapType() {
        if (mMap == null) return;

        switch (currentMapType) {
            case GoogleMap.MAP_TYPE_NORMAL:
                currentMapType = GoogleMap.MAP_TYPE_SATELLITE;
                Toast.makeText(this, "Satellite View", Toast.LENGTH_SHORT).show();
                break;
            case GoogleMap.MAP_TYPE_SATELLITE:
                currentMapType = GoogleMap.MAP_TYPE_TERRAIN;
                Toast.makeText(this, "Terrain View", Toast.LENGTH_SHORT).show();
                break;
            case GoogleMap.MAP_TYPE_TERRAIN:
                currentMapType = GoogleMap.MAP_TYPE_NORMAL;
                Toast.makeText(this, "Normal View", Toast.LENGTH_SHORT).show();
                break;
        }

        mMap.setMapType(currentMapType);
    }

    /**
     * Toggle marker clustering (placeholder for future implementation)
     */
    private void toggleClustering() {
        clusteringEnabled = !clusteringEnabled;
        String message = clusteringEnabled ? "Clustering Enabled" : "Clustering Disabled";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // TODO: Implement marker clustering with ClusterManager
        // This would group nearby markers when zoomed out for better performance
    }

    /**
     * Show filter dialog (placeholder for future implementation)
     */
    private void showFilterDialog() {
        Toast.makeText(this, "Filter options coming soon!", Toast.LENGTH_SHORT).show();

        // TODO: Implement filter dialog for categories, ratings, etc.
    }

    /**
     * Show loading indicator
     */
    private void showLoadingIndicator() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide loading indicator
     */
    private void hideLoadingIndicator() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocationIfPermitted();
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh location when returning to activity
        getCurrentLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources
        if (markerAttractionMap != null) {
            markerAttractionMap.clear();
        }
    }
}

