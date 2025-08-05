package com.s23010526.hiddensrilanka;

import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSeeder {
    private static final String TAG = "DataSeeder";
    private FirebaseFirestore firestore;

    public DataSeeder() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void seedSampleAttractions() {
        // Sample data for Colombo
        seedColomboAttractions();

        // Sample data for Kandy
        seedKandyAttractions();

        // Sample data for Galle
        seedGalleAttractions();
    }

    private void seedColomboAttractions() {
        List<Map<String, Object>> colomboAttractions = new ArrayList<>();

        // Gangaramaya Temple
        Map<String, Object> gangaramaya = new HashMap<>();
        gangaramaya.put("name", "Gangaramaya Temple");
        gangaramaya.put("category", "Temple");
        gangaramaya.put("description", "One of the most important temples in Colombo, known for its eclectic mix of Sri Lankan, Thai, Indian, and Chinese architecture.");
        gangaramaya.put("contributorName", "Data Seeder");
        gangaramaya.put("contributedAt", System.currentTimeMillis());
        gangaramaya.put("youtubeUrl", "");
        List<String> gangaramayaImages = new ArrayList<>();
        gangaramayaImages.add("https://example.com/gangaramaya.jpg");
        gangaramaya.put("images", gangaramayaImages);
        colomboAttractions.add(gangaramaya);

        // Independence Square
        Map<String, Object> independence = new HashMap<>();
        independence.put("name", "Independence Memorial Hall");
        independence.put("category", "Historical Site");
        independence.put("description", "A national monument built to commemorate the independence of Sri Lanka from British rule.");
        independence.put("contributorName", "Data Seeder");
        independence.put("contributedAt", System.currentTimeMillis());
        independence.put("youtubeUrl", "");
        List<String> independenceImages = new ArrayList<>();
        independenceImages.add("https://example.com/independence.jpg");
        independence.put("images", independenceImages);
        colomboAttractions.add(independence);

        // Save to Firestore
        for (Map<String, Object> attraction : colomboAttractions) {
            firestore.collection("cities")
                    .document("Colombo")
                    .collection("attractions")
                    .add(attraction)
                    .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Added Colombo attraction: " + attraction.get("name")))
                    .addOnFailureListener(e ->
                        Log.e(TAG, "Error adding Colombo attraction", e));
        }
    }

    private void seedKandyAttractions() {
        List<Map<String, Object>> kandyAttractions = new ArrayList<>();

        // Temple of the Tooth
        Map<String, Object> tooth = new HashMap<>();
        tooth.put("name", "Temple of the Sacred Tooth Relic");
        tooth.put("category", "Temple");
        tooth.put("description", "A Buddhist temple that houses the relic of the tooth of the Buddha. It's one of the most sacred places of worship for Buddhists.");
        tooth.put("contributorName", "Data Seeder");
        tooth.put("contributedAt", System.currentTimeMillis());
        tooth.put("youtubeUrl", "");
        List<String> toothImages = new ArrayList<>();
        toothImages.add("https://example.com/tooth-temple.jpg");
        tooth.put("images", toothImages);
        kandyAttractions.add(tooth);

        // Royal Botanical Gardens
        Map<String, Object> botanical = new HashMap<>();
        botanical.put("name", "Royal Botanical Gardens Peradeniya");
        botanical.put("category", "More");
        botanical.put("description", "147-acre botanical garden about 5.5 km to the west of the city of Kandy, known for its collection of orchids.");
        botanical.put("contributorName", "Data Seeder");
        botanical.put("contributedAt", System.currentTimeMillis());
        botanical.put("youtubeUrl", "");
        List<String> botanicalImages = new ArrayList<>();
        botanicalImages.add("https://example.com/botanical-gardens.jpg");
        botanical.put("images", botanicalImages);
        kandyAttractions.add(botanical);

        // Save to Firestore
        for (Map<String, Object> attraction : kandyAttractions) {
            firestore.collection("cities")
                    .document("Kandy")
                    .collection("attractions")
                    .add(attraction)
                    .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Added Kandy attraction: " + attraction.get("name")))
                    .addOnFailureListener(e ->
                        Log.e(TAG, "Error adding Kandy attraction", e));
        }
    }

    private void seedGalleAttractions() {
        List<Map<String, Object>> galleAttractions = new ArrayList<>();

        // Galle Fort
        Map<String, Object> fort = new HashMap<>();
        fort.put("name", "Galle Dutch Fort");
        fort.put("category", "Historical Site");
        fort.put("description", "A fortified city built by the Portuguese and later fortified by the Dutch, now a UNESCO World Heritage Site.");
        fort.put("contributorName", "Data Seeder");
        fort.put("contributedAt", System.currentTimeMillis());
        fort.put("youtubeUrl", "");
        List<String> fortImages = new ArrayList<>();
        fortImages.add("https://example.com/galle-fort.jpg");
        fort.put("images", fortImages);
        galleAttractions.add(fort);

        // Unawatuna Beach
        Map<String, Object> unawatuna = new HashMap<>();
        unawatuna.put("name", "Unawatuna Beach");
        unawatuna.put("category", "Beach");
        unawatuna.put("description", "A beautiful crescent-shaped sandy beach, perfect for swimming and snorkeling.");
        unawatuna.put("contributorName", "Data Seeder");
        unawatuna.put("contributedAt", System.currentTimeMillis());
        unawatuna.put("youtubeUrl", "");
        List<String> unawatunaImages = new ArrayList<>();
        unawatunaImages.add("https://example.com/unawatuna.jpg");
        unawatuna.put("images", unawatunaImages);
        galleAttractions.add(unawatuna);

        // Save to Firestore
        for (Map<String, Object> attraction : galleAttractions) {
            firestore.collection("cities")
                    .document("Galle")
                    .collection("attractions")
                    .add(attraction)
                    .addOnSuccessListener(documentReference ->
                        Log.d(TAG, "Added Galle attraction: " + attraction.get("name")))
                    .addOnFailureListener(e ->
                        Log.e(TAG, "Error adding Galle attraction", e));
        }
    }
}
