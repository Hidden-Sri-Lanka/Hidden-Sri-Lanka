# Home & Discovery System

## Overview
The Home screen is the central hub of the Hidden Sri Lanka app, providing location-based attraction discovery with intelligent filtering and search capabilities.

## Features

### 📍 Location-Based Discovery
- **Automatic Location Detection**: Uses GPS to detect user's current city
- **Geocoding Integration**: Converts coordinates to readable city names
- **Sri Lanka Focus**: Optimized for Sri Lankan locations with fallback options
- **Manual Override**: Search functionality for specific cities

### 🔍 Smart Search System
- **City Search**: Search for attractions in any Sri Lankan city
- **Real-time Results**: Instant feedback and loading states
- **Fallback Handling**: Graceful handling when no attractions found
- **Search History**: Integrated with toolbar search field

### 🏠 Home Interface Components
- **Welcome Header**: Personalized greeting with current location
- **Filter Chips**: Horizontally scrollable category filters
- **Attraction Grid**: Vertical scroll list of discovered attractions
- **Loading States**: Progress indicators during data fetching

## Technical Implementation

### Location Services
```java
FusedLocationProviderClient - GPS location detection
Geocoder - Address resolution
Location Permissions - Runtime permission handling
```

### Data Flow
1. **Permission Check**: Request location permissions
2. **Location Detection**: Get current GPS coordinates
3. **Geocoding**: Convert coordinates to city name
4. **Database Query**: Fetch attractions for detected city
5. **UI Update**: Display results with appropriate feedback

### Error Handling
- **No Permission**: Show default attractions
- **No Location**: Fallback to popular cities
- **No Internet**: Cached data display
- **No Attractions**: Community contribution prompt

## User Experience

### Initial Load
1. Splash screen with authentication check
2. Location permission request (if needed)
3. GPS location detection
4. City name resolution
5. Attraction loading with progress indicator

### Location States
- **Detected**: "📍 Detected location: [City Name]"
- **Foreign**: "🌍 Foreign location detected: [City Name]"
- **Manual**: "🔍 Searching attractions in: [Search Query]"
- **Default**: "Loading default attractions..."

### Empty States
When no attractions are found, users see:
- Community contribution prompt
- "Help Us Grow Our Database! 🌟" message
- Encouragement to add local attractions
- Link to Add Location feature

## Search Functionality

### Toolbar Integration
- **Search Field**: Integrated in custom toolbar
- **Search Icon**: Clickable search trigger
- **Auto-complete**: Real-time search suggestions
- **Clear Function**: Easy search field clearing

### Search Behavior
- **Enter Key**: Trigger search on keyboard enter
- **Icon Click**: Alternative search trigger
- **Query Processing**: Trim and validate search terms
- **Results Display**: Replace current attractions list

## Performance Optimizations

### Caching Strategy
- **Location Caching**: Avoid repeated GPS requests
- **Data Caching**: Store fetched attractions locally
- **Image Caching**: Efficient image loading and storage

### Request Management
- **Debouncing**: Prevent excessive location requests
- **Request Flags**: Track ongoing operations
- **Memory Management**: Efficient list handling

## Location Detection Logic

### GPS Flow
```
1. Check permissions
2. Request high-accuracy location
3. Geocode coordinates
4. Normalize city names
5. Query Firebase
6. Display results
```

### Fallback Cities
When location fails, try these cities in order:
- Colombo (capital)
- Kandy (cultural capital)
- Galle (southern gem)
- Anuradhapura (ancient city)
- Nuwara Eliya (hill country)
- Kahawatta (test location)

### City Name Normalization
Handles common variations:
- "Colombo Municipal Council" → "Colombo"
- "Kandy District" → "Kandy"
- Case-insensitive matching
- Special character handling

## Integration Points

### Firebase Connection
- **Firestore Collections**: `/cities/{cityName}/attractions`
- **Real-time Updates**: Live data synchronization
- **Offline Support**: Cached data when offline

### Navigation Integration
- **Deep Linking**: Direct access to specific attractions
- **Search History**: Previous searches accessible
- **Filter State**: Maintained across sessions

---
*The Home & Discovery system provides users with a seamless way to find nearby attractions while maintaining excellent performance and user experience.*
