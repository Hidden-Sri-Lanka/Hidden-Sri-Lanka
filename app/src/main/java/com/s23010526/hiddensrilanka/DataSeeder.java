package com.s23010526.hiddensrilanka;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSeeder {

    private FirebaseFirestore db;

    public DataSeeder() {
        db = FirebaseFirestore.getInstance();
    }

    // Call this method to add sample attractions
    public void seedSampleAttractions() {
        addAttraction(
            "Sigiriya Rock Fortress",
            "Historical Site",
            "An ancient rock fortress and palace built in the 5th century. One of Sri Lanka's most iconic landmarks featuring ancient frescoes and engineering marvels.",
            "https://youtube.com/watch?v=example1",
            Arrays.asList(
                "https://example.com/sigiriya1.jpg",
                "https://example.com/sigiriya2.jpg"
            ),
            "Sigiriya"
        );

        addAttraction(
            "Diyaluma Falls",
            "WaterFall",
            "The second highest waterfall in Sri Lanka, cascading down 220 meters through multiple tiers surrounded by lush greenery.",
            "https://youtube.com/watch?v=example2",
            Arrays.asList(
                "https://example.com/diyaluma1.jpg",
                "https://example.com/diyaluma2.jpg"
            ),
            "Ella"
        );

        addAttraction(
            "Secret Beach Cove",
            "More",
            "A hidden pristine beach accessible only through a jungle path, perfect for those seeking solitude and natural beauty.",
            "https://youtube.com/watch?v=example3",
            Arrays.asList(
                "https://example.com/beach1.jpg"
            ),
            "Galle"
        );
    }

    private void addAttraction(String name, String category, String description,
                              String youtubeUrl, List<String> images, String city) {
        Map<String, Object> attraction = new HashMap<>();
        attraction.put("name", name);
        attraction.put("category", category);
        attraction.put("description", description);
        attraction.put("youtubeUrl", youtubeUrl);
        attraction.put("images", images);
        attraction.put("city", city);

        db.collection("attractions")
            .add(attraction)
            .addOnSuccessListener(documentReference -> {
                System.out.println("Attraction added with ID: " + documentReference.getId());
            })
            .addOnFailureListener(e -> {
                System.err.println("Error adding attraction: " + e);
            });
    }
}
