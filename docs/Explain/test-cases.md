# Allthe Test Done for Hidden Sri Lanka App

## Testing and Results 
1.1 Testing Approach

### Objectives

- To Verify end-to-end flows
```txt
Home → Details → Directions → Open Google Map With Derections 
```

- Ensure reliability of “Intelligent Directions” fallbacks and Google Photos image handling.

- Validate data integrity and UI responsiveness across Android App and versions.

### Test Environments

- Devices
    - Physicle Devices 
        - Sony Xperia XZ 1 - Android 9 + 4GB RAM
        - Nokia 1 - Android 10 Go + 1GB RAM
    - Emulators
        - Pixel 6 - (Android 14)
        - Emulator - API 30(Android 11) + 4GB RAM
- Network
    - Wi‑Fi 
        -2.5GHz
    - Mobile data
        - 4G LTE
        - 3G (H+)
    - Offline 
        - Airplane mode (for offline behavior checks)

- Build
    - Alpha Relese (v1.0.0) --> Developer Testing
    - Beta Relese (v2.0.0) --> Shere across University Students to get feedbacks 
1.2 Types of Testing

- Unit Testing

    - Model validation
        - Attraction fields
            - title
            - description
            - category 
            - images
    - Utilities
        - GooglePhotosUrlHelper output for shared links
            - null/empty inputs
            - malformed URLs

    - Edge cases
- Integration Testing
    - Intent data passing
        - Home --> LocationDetail (verify all extras present and mapped).
    - Firestore reads/writes
        - Attraction listing
        - new location submission
        - real-time updates

    - Image pipeline
        - Glide loading using processed Google Photos URLs across Home and Details.
    - Geocoding flow
        - Address-only entries triggering fallback
        - error handling when API fails.
- System/End-to-End Testing
    - First-time setup
        - Permissions, login, and initial sync.
    - Discover/search/filter → select → details → directions.

    - Add Location with various data combinations
        - valid/invalid photo links
    - Sign-in/out

- User (Usability) Testing
    - Participants
        - Developer 
        - Open University Students
    - Tasks
        - Find a place near [City]--> open Add new location Feture --> add a new location with photos.
    - Measures
        - Task completion
        - time on task
        - issues reported.
1.3 Test Cases and Outcomes (summarized)

- TC-01
    - Test
        - Open app
        - allow location
        - view nearby list
    - Expected
        - List loads within 2–4s
        - relevant places appear
    - Result
        - Pass on Wi‑Fi
        - minor delay on mobile data

- TC-02
    - Test
        - Search “waterfall”
        - apply filter “Nature”
    - Expected
        - Filtered list with relevant attractions
    - Result
        - Pass
- TC-03
    - Test
        - Open details
        - verify title
        - images
        - description
    - Expected
        - All fields present
        - images render
    - Result
        - Pass

- TC-04
    - Test
        - Tap Directions Button 
    - Expected
        - Maps opens with route
    - Result: Pass

- TC-05
    - Test
        - Add Location with valid Google Photos link
    - Expected
        - New record saved
        - image displays in Details
    - Result
        - Pass
- TC-06
    - Test
        - Add Location with private Google Photos link
    - Expected
        - Image fails
        - clear error message
    - Result
        - Pass 
- TC-07
    - Test
        - Login/Sign up
    - Expected
        - Flows complete
    - Result
        - Pass

- TC-08
    - Test
        - Poor connectivity
    - Expected
        - Graceful loading states
        - retry works
    - Result
        -Pass (longer image load times observed)

1.4 Key Findings and Fixes

- Intent Extras Consistency

    - Issue
        - Details screen showed empty data in earlier builds.

    - Fix
        - Standardized extra keys between AttractionAdapter and LocationDetailActivity.

    - Result
        - Details consistently populated.

- Google Photos Image Handling

    - Issue
        - Some shared links did not render.

    - Fix
        - Added URL processing (normalization) before Glide loads
        - improved error messages.

    - Result
        - High success rate with publicly shared links
        -helpful guidance on failures.

- Intelligent Directions Fallback

    - Enhancement
        - Multi-level fallback—coordinates ( geocode address → manual prompt)

    - Result
        - Directions reliable even for partial submissions.

1.5 User Feedback Summary

    - Positives
        - Simple UI
        - fast discovery
        - reliable directions.

    - Suggestions
        - Add favorites
        - offline cache for previously viewed places
        - Sinhala/Tamil interface.
        - dark Mode 
        - Pin Location On Google map


    - Known Issues
        - Images depend on public accessibility of Google Photos links.
        - Performance can vary with low bandwidth (images slower to render)
        - 

Auther : @Asitha Kanchana
Date :202/08/19
Updated : 

