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

import com.bumptech.glide.Glide;
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

    private RecyclerView recyclerView;
    private AttractionAdapter adapter;
    private List<Attraction> attractionList;

    private ProgressBar progressBar;
    private ChipGroup chipGroup;

    private FirebaseFirestore firestoreDb;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private String currentCity = null;

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
        recyclerView = findViewById(R.id.recyclerView_attractions);

        attractionList = new ArrayList<>();
        adapter = new AttractionAdapter(attractionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        checkLocationPermission();
        setupFilterListener();
    }
//--------------------------------------------------------------------------------------------------------------------------------------------------------
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
                Toast.makeText(this, "Location permission is required to show nearby attractions. Please Allow ", Toast.LENGTH_LONG).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentCity() {
        progressBar.setVisibility(View.VISIBLE);

        fusedLocationProviderClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {

                                String city = addresses.get(0).getLocality();
                                if (city == null) {
                                    city = addresses.get(0).getSubAdminArea();
                                }

                                if (city != null) {
                                    currentCity = city;
                                    // checking  REAL city now to make the app dynamic
                                    loadAttractionsFromFirestore(currentCity, "All");
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.GONE);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Could not get last location. Please Check your  location is on.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(this, e -> {
                    progressBar.setVisibility(View.GONE);

                    // Display helpful error message
                    String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown location error.";
                    Toast.makeText(this, "Error getting location: " + errorMessage, Toast.LENGTH_LONG).show();
                });
    }

//_________---------------------------------------------------------------------------------------------------------------------------------
    //Data Fetching and Filtering Methods ---

    private void loadAttractionsFromFirestore(String cityName, String category) {
        progressBar.setVisibility(View.VISIBLE);
        attractionList.clear();

        // trim whitespace from the city name
        String formattedCityName = cityName.trim();
        Log.d("FirestoreQuery", "Querying for city: '" + formattedCityName + "' and category: '" + category + "'");

        Query query = firestoreDb.collection("cities").document(formattedCityName).collection("attractions");

        if (!"All".equalsIgnoreCase(category)) {
            query = query.whereEqualTo("category", category);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirestoreSuccess", "Task successful. Found " + task.getResult().size() + " documents.");
                if (task.getResult().isEmpty()) {
                    Toast.makeText(HomeActivity.this, "No attractions found for " + formattedCityName, Toast.LENGTH_SHORT).show();
                }

                for (QueryDocumentSnapshot document : task.getResult()) {
                    Attraction attraction = document.toObject(Attraction.class);
                    attraction.setDocumentId(document.getId()); // Save the ID for clicks
                    attractionList.add(attraction);
                    Log.d("FirestoreData", "Added attraction: " + attraction.getName());
                }
                adapter.notifyDataSetChanged();
            } else {
                Log.e("FirestoreError", "Error getting documents: ", task.getException());
                Toast.makeText(HomeActivity.this, "Error loading data.", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    private void setupFilterListener() {
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == View.NO_ID) {
                // This can happen when the filter is cleared
                return;
            }
            Chip selectedChip = findViewById(checkedId);
            if (selectedChip != null && currentCity != null) {
                String selectedCategory = selectedChip.getText().toString();
                loadAttractionsFromFirestore(currentCity, selectedCategory);
            }
        });
    }
}