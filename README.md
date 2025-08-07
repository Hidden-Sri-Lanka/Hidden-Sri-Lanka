# Hidden Sri Lanka 🇱🇰

> Discover the wonders beyond the guidebook

A mobile application for discovering hidden gems and lesser-known attractions across Sri Lanka, powered by community contributions and location-based recommendations.

**Academic Project**: Mobile Application Development  
**Institution**: Open University of Sri Lanka (OUSL)  
**Degree Program**: Bachelor of Software Engineering  
**Project Focus**: Android development with Firebase integration

## 📱 Features

- **🌍 Location-Based Discovery**: Automatically detects your location and shows nearby attractions
- **🔍 Smart Filtering**: Filter attractions by categories (Historical Sites, Waterfalls, Beaches, etc.)
- **➕ Community Contributions**: Add new hidden gems with Google Photos integration
- **📍 Intelligent Directions**: Three-level fallback system for navigation (coordinates → geocoding → search)
- **🖼️ Google Photos Integration**: Seamless image sharing using Google Photos links
- **🔐 Persistent Login**: Stay logged in across app sessions
- **📱 Manual Search**: Search for attractions in specific Sri Lankan cities
- **🎨 Modern UI**: Clean, intuitive Material Design interface
- **📲 Robust Error Handling**: Comprehensive fallback systems for reliability

## 🔧 Recent Improvements

As part of my development process, I've implemented several key fixes and enhancements:

### Data Flow Fixes
- **Intent Data Passing**: Resolved issue where location details page showed empty data due to mismatched intent keys
- **Image Loading**: Fixed Google Photos URLs not displaying in location details by adding proper URL processing
- **Navigation System**: Implemented intelligent directions with geocoding fallback when coordinates are missing

### User Experience Enhancements
- **Smart Directions**: Now works even when location coordinates aren't stored in database
- **Image Support**: Enhanced Google Photos integration across all screens
- **Error Recovery**: Added comprehensive error handling with user-friendly fallbacks

## 🚀 Quick Start

### Prerequisites
- Android Studio Arctic Fox (2020.3.1) or newer
- Android SDK API 21+ (Android 5.0+)
- Firebase account for backend services

### Installation
1. **Clone the repository**
   ```bash
   git clone https://github.com/Hidden-Sri-Lanka/Hidden-Sri-Lanka.git
   cd Hidden-Sri-Lanka
   ```

2. **Setup Firebase**
   - Create a Firebase project
   - Add your `google-services.json` to `app/` directory
   - Enable Firestore and Realtime Database
   - Configure authentication

3. **Build and Run**
   ```bash
   ./gradlew clean build
   ./gradlew installDebug
   ```

## 🏗️ Architecture & Technical Details

This project demonstrates several key Android development concepts learned during my studies:

### Core Technologies
- **Android SDK**: Native Android development with Java
- **Firebase**: Real-time database and authentication
- **Glide**: Efficient image loading and caching
- **Material Design**: Modern UI/UX principles
- **Location Services**: GPS integration and geocoding
- **Intent System**: Activity communication and data passing

### Key Learning Outcomes
- **Activity Lifecycle**: Proper management of Android activity states
- **Background Processing**: Threading for network operations and geocoding
- **Error Handling**: Implementing robust fallback systems
- **Database Integration**: Firestore integration with real-time updates
- **Image Processing**: Google Photos URL conversion and image loading
- **User Experience**: Creating intuitive navigation and feedback systems

## 📚 Documentation

### Core Features
- [🔐 Authentication System](docs/features/authentication.md) - Login, signup, and session management
- [🏠 Home & Discovery](docs/features/home-discovery.md) - Location detection and attraction browsing
- [🔍 Filtering System](docs/features/filtering.md) - Category-based attraction filtering
- [📍 Location Details](docs/features/location-details.md) - Detailed attraction information with intelligent directions
- [➕ Add Locations](docs/features/add-locations.md) - Community contribution system with Google Photos
- [🧭 Navigation](docs/features/navigation.md) - App navigation and menu system
- [🖼️ Google Photos Integration](docs/features/google-photos-integration.md) - Image sharing and URL processing

### Development Guides
- [📋 Installation Guide](docs/setup/installation.md) - Complete setup instructions
- [🏗️ Architecture Overview](docs/setup/architecture.md) - Technical architecture and design patterns
- [🔥 Firebase Configuration](docs/setup/firebase.md) - Database and authentication setup
- [🐛 Troubleshooting](docs/guides/troubleshooting.md) - Common issues and solutions

## 🎓 Academic Context

This application serves as my final project for the Mobile Application Development course at OUSL. The project showcases:

### Learning Objectives Met
- **Android Fundamentals**: Activities, intents, and lifecycle management
- **Database Integration**: Real-time data synchronization with Firestore
- **Location Services**: GPS integration and geocoding APIs
- **Image Processing**: Efficient loading and Google Photos integration
- **User Interface Design**: Material Design principles and responsive layouts
- **Error Management**: Comprehensive error handling and user feedback
- **Testing & Debugging**: Systematic approach to identifying and fixing issues

### Development Challenges Overcome
1. **Data Passing Between Activities**: Resolved intent key mismatches that prevented proper data flow
2. **Image Loading Issues**: Implemented Google Photos URL processing for seamless image display
3. **Navigation Reliability**: Created intelligent fallback system for directions when coordinates are missing
4. **Performance Optimization**: Implemented efficient background processing for geocoding operations

## 🤝 Contributing

While this is primarily an academic project, I welcome feedback and suggestions from fellow students and developers. If you find any issues or have improvement suggestions:

1. Open an issue describing the problem or enhancement
2. For bug reports, include device information and reproduction steps
3. Feel free to suggest new features that could enhance the learning experience

## 📞 Contact

**Student**: [Your Name]  
**Institution**: Open University of Sri Lanka  
**Program**: Bachelor of Software Engineering  
**Course**: Mobile Application Development  

For academic inquiries or technical questions about this project, please use the GitHub issues section.

## 📄 License

This project is developed for academic purposes as part of my degree program at OUSL. Please respect the educational nature of this work.

---

*Developed with dedication to showcasing practical Android development skills and contributing to Sri Lanka's tourism discovery platform.*
