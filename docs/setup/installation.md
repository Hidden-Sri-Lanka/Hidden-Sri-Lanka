# Installation & Setup Guide

## Prerequisites

### Development Environment
- **Android Studio**: Arctic Fox (2020.3.1) or newer
- **JDK**: Java Development Kit 8 or higher
- **Android SDK**: API level 21 (Android 5.0) minimum, API level 35 (Android 14) target
- **Gradle**: 8.4.1 or compatible version
- **Git**: For version control and repository cloning

### Hardware Requirements
- **RAM**: Minimum 8GB, recommended 16GB
- **Storage**: At least 10GB free space
- **Processor**: Intel i5 or equivalent AMD processor
- **Graphics**: Integrated graphics sufficient for Android emulator

## Installation Steps

### 1. Clone Repository
```bash
git clone https://github.com/your-username/Hidden-Sri-Lanka.git
cd Hidden-Sri-Lanka
```

### 2. Open in Android Studio
1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned repository folder
4. Click "OK" to open the project
5. Wait for Gradle sync to complete

### 3. Configure Build Environment
```bash
# Check Gradle version compatibility
./gradlew --version

# Clean and build project
./gradlew clean
./gradlew build
```

### 4. Setup Android SDK
1. Open SDK Manager in Android Studio
2. Install required SDK platforms:
   - Android 14 (API 35) - Target SDK
   - Android 5.0 (API 21) - Minimum SDK
3. Install required tools:
   - Android SDK Build-Tools
   - Android Emulator
   - Google Play Services

## Firebase Configuration

### 1. Create Firebase Project
1. Visit [Firebase Console](https://console.firebase.google.com)
2. Click "Create a project"
3. Enter project name: "Hidden Sri Lanka"
4. Enable Google Analytics (optional)
5. Complete project creation

### 2. Add Android App
1. Click "Add app" → Android icon
2. Enter package name: `com.s23010526.hiddensrilanka`
3. Enter app nickname: "Hidden Sri Lanka"
4. Download `google-services.json`
5. Place file in `app/` directory

### 3. Configure Firebase Services
Enable the following services in Firebase Console:
- **Authentication**: For user login/signup
- **Firestore Database**: For attraction data
- **Realtime Database**: For user authentication data
- **Storage**: For image uploads (future feature)

## Project Structure Setup

### 1. Verify Directory Structure
```
Hidden-Sri-Lanka/
├── app/
│   ├── src/main/java/com/s23010526/hiddensrilanka/
│   ├── src/main/res/
│   ├── google-services.json
│   └── build.gradle.kts
├── docs/ (created)
├── gradle/
└── build.gradle.kts
```

### 2. Check Dependencies
Verify these dependencies in `app/build.gradle.kts`:
```kotlin
implementation 'com.google.firebase:firebase-firestore:24.4.1'
implementation 'com.google.firebase:firebase-database:20.0.4'
implementation 'com.google.firebase:firebase-auth:21.0.3'
implementation 'com.google.android.gms:play-services-location:21.0.1'
implementation 'com.github.bumptech.glide:glide:4.14.2'
```

## Local Configuration

### 1. Create local.properties
```properties
# Local development settings
sdk.dir=/path/to/Android/Sdk
firebase.database.url=your-firebase-database-url
```

### 2. Configure Signing (Optional)
For release builds, create `keystore.properties`:
```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=your_key_alias
storeFile=path/to/keystore.jks
```

### 3. Environment Variables
Set environment variables if needed:
```bash
export ANDROID_HOME=/path/to/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

## Testing Setup

### 1. Run on Emulator
1. Create Android Virtual Device (AVD)
2. Select API level 30 or higher
3. Choose device with Google Play Services
4. Launch emulator and run app

### 2. Run on Physical Device
1. Enable Developer Options on device
2. Enable USB Debugging
3. Connect device via USB
4. Accept debugging permission
5. Run app from Android Studio

### 3. Test Location Services
- Grant location permissions when prompted
- Use emulator extended controls for location simulation
- Test with different mock locations (Colombo, Kandy, etc.)

## Common Issues & Solutions

### Gradle Sync Issues
```bash
# Clear Gradle cache
./gradlew clean
rm -rf ~/.gradle/caches/

# Re-download dependencies
./gradlew build --refresh-dependencies
```

### Firebase Connection Issues
1. Verify `google-services.json` is in correct location
2. Check package name matches Firebase configuration
3. Ensure Firebase services are enabled
4. Verify internet connection and firewall settings

### Location Permission Issues
1. Grant location permissions in device settings
2. Check GPS is enabled on device/emulator
3. Verify location services in app permissions
4. Test with mock locations for development

### Build Errors
Common solutions:
- Update Android Studio to latest version
- Sync Gradle files
- Invalidate caches and restart IDE
- Check SDK versions compatibility
- Update dependencies to latest versions

## Development Workflow

### 1. Branch Strategy
```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make changes and commit
git add .
git commit -m "Add your feature description"

# Push and create pull request
git push origin feature/your-feature-name
```

### 2. Code Style
- Follow Android Kotlin Style Guide
- Use meaningful variable and method names
- Add comments for complex logic
- Maintain consistent indentation (4 spaces)

### 3. Testing
- Test on multiple device sizes
- Verify location services functionality
- Test offline/online scenarios
- Validate form inputs and edge cases

## Deployment Preparation

### 1. Release Build
```bash
# Generate signed APK
./gradlew assembleRelease

# Generate AAB for Play Store
./gradlew bundleRelease
```

### 2. Version Management
Update version in `app/build.gradle.kts`:
```kotlin
android {
    defaultConfig {
        versionCode 1
        versionName "1.0.0"
    }
}
```

### 3. ProGuard Configuration
Ensure proper obfuscation rules in `proguard-rules.pro`

---
*Follow this guide to set up your development environment and start contributing to the Hidden Sri Lanka project.*
