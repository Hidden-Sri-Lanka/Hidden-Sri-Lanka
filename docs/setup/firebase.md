# Firebase Integration Guide

## Overview
Hidden Sri Lanka uses Firebase as its backend-as-a-service platform, providing authentication, real-time database, and cloud storage capabilities.

## Firebase Services Used

### 1. Firebase Realtime Database
**Purpose**: User authentication and profile data
**Database URL**: `https://hidden-sri-lanka-c3ec5-default-rtdb.asia-southeast1.firebasedatabase.app`

#### Database Structure
```json
{
  "users": {
    "user-id-1": {
      "username": "john_doe",
      "email": "john@example.com",
      "name": "John Doe",
      "password": "encrypted_password"
    }
  }
}
```

#### Security Rules
```javascript
{
  "rules": {
    "users": {
      ".read": "auth != null",
      ".write": "auth != null",
      "$uid": {
        ".validate": "newData.hasChildren(['username', 'email', 'name'])"
      }
    }
  }
}
```

### 2. Cloud Firestore
**Purpose**: Attraction data and location information

#### Database Structure
```
/cities/{cityName}/attractions/{attractionId}
{
  name: string,
  category: string,
  description: string,
  images: array<string>,
  youtubeUrl: string,
  contributorName: string,
  contributedAt: timestamp,
  province: string,
  city: string,
  latitude: number,
  longitude: number
}
```

#### Example Document
```json
{
  "name": "Sigiriya Rock Fortress",
  "category": "Historical Site",
  "description": "Ancient rock fortress and palace ruins...",
  "images": [
    "https://example.com/sigiriya1.jpg",
    "https://example.com/sigiriya2.jpg"
  ],
  "youtubeUrl": "https://youtube.com/watch?v=example",
  "contributorName": "Travel Expert",
  "contributedAt": 1691234567890,
  "province": "Central Province",
  "city": "Dambulla",
  "latitude": 7.9568,
  "longitude": 80.7592
}
```

### 3. Firebase Authentication (Future Enhancement)
**Current Status**: Not implemented yet
**Planned Features**: 
- Email/password authentication
- Google Sign-In
- Phone number verification

## Setup Instructions

### 1. Firebase Project Configuration

#### Create Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click "Create a project"
3. Project name: `hidden-sri-lanka`
4. Enable Google Analytics (recommended)
5. Choose your region (Asia-Southeast1 for Sri Lanka)

#### Enable Required Services
1. **Realtime Database**:
   - Navigate to "Realtime Database" in sidebar
   - Click "Create Database"
   - Choose "Asia-Southeast1" region
   - Start in "Test Mode" initially

2. **Cloud Firestore**:
   - Navigate to "Firestore Database"
   - Click "Create Database"
   - Choose "Asia-Southeast1" region
   - Start in "Test Mode"

3. **Authentication** (Future):
   - Navigate to "Authentication"
   - Click "Get Started"
   - Enable Email/Password provider

### 2. Android App Configuration

#### Add Android App to Firebase
1. Click "Add app" → Android icon
2. **Package name**: `com.s23010526.hiddensrilanka`
3. **App nickname**: "Hidden Sri Lanka"
4. **Debug signing certificate**: Optional for development
5. Download `google-services.json`

#### Install google-services.json
```bash
# Place the file in your app module
cp ~/Downloads/google-services.json app/google-services.json
```

#### Update build.gradle files
**Project-level build.gradle.kts**:
```kotlin
plugins {
    id("com.google.gms.google-services") version "4.3.15" apply false
}
```

**App-level build.gradle.kts**:
```kotlin
plugins {
    id("com.google.gms.google-services")
}

dependencies {
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-auth:22.3.0")
}
```

### 3. Security Rules Setup

#### Realtime Database Rules
```javascript
{
  "rules": {
    "users": {
      ".read": true,
      ".write": true,
      "$userId": {
        ".validate": "newData.hasChildren(['username', 'email', 'name', 'password'])",
        "username": {
          ".validate": "newData.isString() && newData.val().length > 0"
        },
        "email": {
          ".validate": "newData.isString() && newData.val().matches(/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$/i)"
        },
        "name": {
          ".validate": "newData.isString() && newData.val().length > 0"
        },
        "password": {
          ".validate": "newData.isString() && newData.val().length >= 6"
        }
      }
    }
  }
}
```

#### Firestore Security Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow read access to all cities and attractions
    match /cities/{city}/attractions/{attraction} {
      allow read: if true;
      allow write: if true; // Temporary for development
    }
  }
}
```

## Data Management

### 1. Adding Sample Data

#### Using Firebase Console
1. Navigate to Firestore Database
2. Start collection: `cities`
3. Document ID: City name (e.g., "Colombo")
4. Add subcollection: `attractions`
5. Add attraction documents with required fields

#### Using Android Code
```java
FirebaseFirestore db = FirebaseFirestore.getInstance();

Map<String, Object> attraction = new HashMap<>();
attraction.put("name", "Galle Fort");
attraction.put("category", "Historical Site");
attraction.put("description", "Historic fortress built by Portuguese...");
attraction.put("contributorName", "Admin");
attraction.put("contributedAt", System.currentTimeMillis());

db.collection("cities")
  .document("Galle")
  .collection("attractions")
  .add(attraction);
```

### 2. Data Validation

#### Client-side Validation
```java
public boolean validateAttraction(Attraction attraction) {
    return attraction.getName() != null && !attraction.getName().isEmpty() &&
           attraction.getCategory() != null && !attraction.getCategory().isEmpty() &&
           attraction.getDescription() != null && attraction.getDescription().length() >= 50 &&
           attraction.getImages() != null && !attraction.getImages().isEmpty();
}
```

#### Server-side Validation (Cloud Functions)
```javascript
exports.validateAttraction = functions.firestore
    .document('cities/{cityId}/attractions/{attractionId}')
    .onWrite((change, context) => {
        const attraction = change.after.data();
        
        if (!attraction.name || attraction.name.length < 3) {
            throw new Error('Invalid attraction name');
        }
        
        return null;
    });
```

## Performance Optimization

### 1. Firestore Indexes
Create composite indexes for common queries:
```
Collection: cities/{cityId}/attractions
Fields: category (Ascending), contributedAt (Descending)
```

### 2. Data Caching
```java
FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
    .setPersistenceEnabled(true)
    .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
    .build();
db.setFirestoreSettings(settings);
```

### 3. Offline Support
```java
db.enableNetwork().addOnCompleteListener(task -> {
    // Re-enable network
});

db.disableNetwork().addOnCompleteListener(task -> {
    // Disable network, use cached data
});
```

## Monitoring & Analytics

### 1. Firebase Analytics
```java
FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);

Bundle bundle = new Bundle();
bundle.putString("attraction_name", attractionName);
bundle.putString("city", cityName);
analytics.logEvent("attraction_viewed", bundle);
```

### 2. Crashlytics (Recommended)
```kotlin
// Add to build.gradle.kts
implementation("com.google.firebase:firebase-crashlytics:18.6.0")
```

### 3. Performance Monitoring
```kotlin
implementation("com.google.firebase:firebase-perf:20.5.1")
```

## Testing

### 1. Firebase Emulator Suite
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Initialize emulators
firebase init emulators

# Start emulators
firebase emulators:start
```

### 2. Test Data Setup
```java
// Use emulator in debug builds
if (BuildConfig.DEBUG) {
    FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080);
    FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
}
```

## Backup & Migration

### 1. Data Export
```bash
# Export Firestore data
gcloud firestore export gs://your-bucket-name

# Export Realtime Database
firebase database:get / --output backup.json
```

### 2. Data Import
```bash
# Import to Firestore
gcloud firestore import gs://your-bucket-name/backup-folder

# Import to Realtime Database
firebase database:set / backup.json
```

## Troubleshooting

### Common Issues
1. **Permission Denied**: Check security rules
2. **Network Issues**: Verify internet connection
3. **Quota Exceeded**: Monitor Firebase usage
4. **Sync Issues**: Clear app data and re-sync

### Debug Tools
- Firebase Console → Usage tab
- Android Studio → Firebase Assistant
- Firebase CLI for local testing
- Network inspection tools

---
*This guide provides comprehensive Firebase integration for the Hidden Sri Lanka app with proper security, performance, and maintenance considerations.*
