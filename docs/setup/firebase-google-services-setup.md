# Firebase Setup: Adding google-services.json

## Overview
The `google-services.json` file is a configuration file that connects your Android app to your Firebase project. This file contains essential information like project IDs, API keys, and database URLs that Firebase services need to function properly.

## Prerequisites
- A Google account
- Access to [Firebase Console](https://console.firebase.google.com/)
- Android Studio or development environment set up

## Step-by-Step Instructions

### 1. Create or Access Firebase Project
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Sign in with your Google account
3. Either:
   - **Create New Project**: Click "Create a project" and follow the setup wizard
   - **Use Existing Project**: Select your existing Firebase project

### 2. Add Android App to Firebase Project
1. In your Firebase project dashboard, click the **Android icon** (🤖) or "Add app"
2. Fill in the required information:
   - **Android package name**: `com.s23010526.hiddensrilanka`
     - ⚠️ **Important**: This must match exactly with the `applicationId` in your `app/build.gradle.kts`
   - **App nickname** (optional): `Hidden Sri Lanka`
   - **Debug signing certificate SHA-1** (optional for now, required for some features)

### 3. Download google-services.json
1. After registering your app, Firebase will generate the `google-services.json` file
2. Click **"Download google-services.json"**
3. The file will be downloaded to your computer

### 4. Add File to Your Project
1. **Locate the file**: Find the downloaded `google-services.json` file (usually in your Downloads folder)
2. **Copy to project**: Move/copy the file to your app module directory:
   ```
   Hidden-Sri-Lanka/
   └── app/
       ├── src/
       ├── build.gradle.kts
       └── google-services.json  ← Place it here
   ```
3. **Verify placement**: The file should be at the same level as your `app/build.gradle.kts` file

### 5. Verify Configuration
1. **Check file contents**: Open `google-services.json` and verify it contains:
   - Your project information
   - Correct package name (`com.s23010526.hiddensrilanka`)
   - Firebase configuration details

2. **Gradle plugin verification**: Ensure your `app/build.gradle.kts` has:
   ```kotlin
   plugins {
       // ... other plugins
       alias(libs.plugins.google.gms.google.services)
   }
   ```

### 6. Sync and Build
1. **Sync project**: In Android Studio, click "Sync Now" or "Sync Project with Gradle Files"
2. **Clean build**: Run `./gradlew clean build` to ensure everything compiles correctly

## Security Notes

### ✅ Safe to Commit
The `google-services.json` file is **safe to commit to version control** because:
- It contains public configuration data, not secret keys
- It's designed to be included in your app bundle
- Firebase services expect this file to be present in the APK

### 🔒 What's Inside
The file typically contains:
- Project ID (public)
- Application ID (public)
- API keys for client-side services (public)
- Database URLs (public endpoints)
- Storage bucket names (public)

## Common Issues & Solutions

### Issue: "google-services.json not found"
**Solution**: Ensure the file is placed directly in the `app/` directory, not in subdirectories

### Issue: "Package name mismatch"
**Solution**: 
1. Check that the package name in `google-services.json` matches your `applicationId`
2. If they don't match, re-download the file with the correct package name from Firebase Console

### Issue: "Multiple google-services.json files"
**Solution**: Only keep one `google-services.json` file in the `app/` directory

### Issue: "Firebase not initialized"
**Solution**: 
1. Verify the file is in the correct location
2. Check that the Google Services plugin is applied
3. Sync the project and rebuild

## File Structure After Setup
```
Hidden-Sri-Lanka/
├── app/
│   ├── src/
│   ├── build.gradle.kts
│   └── google-services.json  ✅ Correctly placed
├── build.gradle.kts
└── local.properties
```

## Firebase Services Configuration
Once the `google-services.json` is properly added, these Firebase services will work:
- 🔥 **Authentication** (Login/Signup)
- 📊 **Firestore** (Location data storage)
- 🗄️ **Realtime Database** (User data)
- 📦 **Cloud Storage** (Image uploads)

## Next Steps
After adding `google-services.json`:
1. **Enable Firebase services** in the Firebase Console that your app uses
2. **Set up Firebase rules** for Firestore and Storage
3. **Test Firebase connection** by running the app
4. **Configure authentication providers** (Email/Password, Google Sign-In)

## Troubleshooting Commands
```bash
# Clean and rebuild project
./gradlew clean build

# Check if Firebase is properly configured
./gradlew app:dependencies | grep firebase

# Verify Google Services plugin is applied
./gradlew app:dependencies | grep google-services
```

## Additional Resources
- [Firebase Android Setup Guide](https://firebase.google.com/docs/android/setup)
- [Firebase Console](https://console.firebase.google.com/)
- [Troubleshooting Firebase Setup](https://firebase.google.com/docs/android/setup#troubleshooting)

---
**Note**: This file is specific to your Firebase project. Each developer working on the project should use the same `google-services.json` file to ensure consistent Firebase configuration.
