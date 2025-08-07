# Hidden Sri Lanka Mobile App

## 📱 Android Application Module

This directory contains the main Android application code for the Hidden Sri Lanka project, developed as part of my Mobile Application Development course at OUSL.

## 🏗️ Project Structure

```
app/
├── src/main/java/com/s23010526/hiddensrilanka/
│   ├── Activities/
│   │   ├── MainActivity.java
│   │   ├── HomeActivity.java
│   │   ├── LocationDetailActivity.java
│   │   ├── AddLocationActivity.java
│   │   └── LoginActivity.java
│   ├── Adapters/
│   │   └── AttractionAdapter.java
│   ├── Models/
│   │   └── Attraction.java
│   ├── Helpers/
│   │   └── GooglePhotosUrlHelper.java
│   └── Utils/
└── src/main/res/
    ├── layout/
    ├── values/
    └── drawable/
```

## 🔧 Technical Implementation

### Core Components

#### Activities
- **HomeActivity**: Location detection, attraction display, and filtering
- **LocationDetailActivity**: Detailed attraction view with intelligent directions
- **AddLocationActivity**: Community contribution form with Google Photos integration
- **LoginActivity**: Authentication and session management

#### Key Classes
- **AttractionAdapter**: RecyclerView adapter with proper intent data passing
- **GooglePhotosUrlHelper**: URL processing for seamless image integration
- **Attraction**: Data model with comprehensive field mapping

### Recent Development Work

#### Bug Fixes Implemented
1. **Intent Data Passing Issue** (Fixed: August 2025)
   - Problem: Location details page showing empty data
   - Solution: Corrected intent extra keys between activities
   - Files modified: `AttractionAdapter.java`, `LocationDetailActivity.java`

2. **Google Photos Image Loading** (Fixed: August 2025)
   - Problem: Images not displaying in details page
   - Solution: Added URL processing to LocationDetailActivity
   - Files modified: `LocationDetailActivity.java`

3. **Directions Button Functionality** (Enhanced: August 2025)
   - Problem: Button not working when coordinates missing
   - Solution: Implemented three-level fallback system
   - Files modified: `LocationDetailActivity.java`

#### Technical Improvements
- **Smart Geocoding**: Automatic coordinate resolution for missing location data
- **Error Handling**: Comprehensive exception management across all activities
- **Background Processing**: Proper threading for network operations
- **Lambda Expression Fixes**: Resolved compilation issues with final variables

## 🎯 Learning Outcomes Demonstrated

### Android Development Skills
- **Activity Communication**: Proper intent usage and data passing
- **Lifecycle Management**: Handling activity states and background operations
- **UI/UX Design**: Material Design implementation with responsive layouts
- **Database Integration**: Firestore real-time data synchronization
- **Location Services**: GPS integration and geocoding API usage
- **Image Processing**: Efficient loading with Glide and URL conversion

### Problem-Solving Approach
- **Systematic Debugging**: Using logs and systematic testing to identify issues
- **Fallback Systems**: Implementing robust error recovery mechanisms
- **User Experience**: Ensuring functionality works under various conditions
- **Code Quality**: Proper variable scoping and lambda expression handling

## 🔥 Firebase Configuration

The app integrates with Firebase for:
- **Firestore Database**: Real-time attraction data storage
- **Authentication**: User login and session management
- **Security Rules**: Proper data access control

Configuration file: `google-services.json` (not included in repository for security)

## 🧪 Testing Approach

### Manual Testing Scenarios
- Location detection across different cities
- Image loading with various URL formats
- Directions functionality with and without coordinates
- Error handling under poor network conditions
- Cross-activity data flow validation

### Debug Implementation
- Comprehensive logging throughout the application
- Error state handling with user-friendly messages
- Performance monitoring for image loading and geocoding

## 📊 Performance Considerations

### Optimizations Implemented
- **Image Caching**: Glide library for efficient memory usage
- **Background Processing**: Non-blocking UI for network operations
- **Database Queries**: Efficient Firestore query structure
- **Memory Management**: Proper activity lifecycle handling

## 🚀 Build Configuration

- **Target SDK**: API 34 (Android 14)
- **Minimum SDK**: API 21 (Android 5.0)
- **Build Tools**: Gradle with modern Android build system
- **Dependencies**: Material Design, Firebase, Glide, Location Services

## 📝 Change Log

### August 2025
- **Intent Data Passing Fix**: Resolved empty details page issue
- **Google Photos Integration**: Enhanced image loading across all screens
- **Intelligent Directions**: Implemented geocoding fallback system
- **Error Handling**: Added comprehensive exception management
- **Code Quality**: Fixed lambda expression compilation issues

### June 2025
- **Initial Development**: GitHub repository initialization
- **Core Features**: Basic app structure and Firebase integration
- **Author**: Asitha Kanchana (Student ID: S23010526)

## 🎓 Academic Context

This mobile application demonstrates practical implementation of concepts learned in the Mobile Application Development course at OUSL, including:

- Modern Android development practices
- Real-time database integration
- Location-based services
- Image processing and optimization
- Error handling and user experience design
- Systematic debugging and problem-solving

---

**Developer**: Asitha Kanchana  
**Student ID**: S23010526  
**Institution**: Open University of Sri Lanka  
**Course**: Mobile Application Development  
**Degree**: Bachelor of Software Engineering
