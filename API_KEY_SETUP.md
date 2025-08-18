# Google Maps API Key Security Setup

## Overview
This project secures the Google Maps API key by storing it in `local.properties` instead of exposing it in the codebase. This prevents the API key from being committed to version control.

## Setup Instructions

### For New Developers
1. Copy `local.properties.template` to `local.properties`
2. Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual Google Maps API key
3. The `local.properties` file is automatically ignored by git

### Getting Your Google Maps API Key
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the Maps SDK for Android API
4. Create credentials (API Key)
5. Restrict the API key to Android apps and add your package name

### How It Works
- The API key is stored in `local.properties` (not committed to git)
- `build.gradle.kts` reads the key and injects it as a BuildConfig field
- `AndroidManifest.xml` uses a placeholder that gets replaced during build
- Your app can access the key via `BuildConfig.GOOGLE_MAPS_API_KEY` if needed

### Security Benefits
- ✅ API key is not exposed in public repositories
- ✅ Each developer uses their own API key
- ✅ Production builds can use environment-specific keys
- ✅ No risk of accidental commits of sensitive data

## File Structure
```
local.properties          # Your actual API key (ignored by git)
local.properties.template # Template for other developers
```

## Important Notes
- Never commit `local.properties` to version control
- Always use the template file for documentation
- Consider using different API keys for debug/release builds
