# Home & Discovery Feature

## 🏠 **Feature Overview**
The home screen is the main entry point where users discover attractions near their location through automatic detection and filtering.

## 🎯 **Simple Explanation for Viva**
*"The home screen automatically detects where you are and shows nearby attractions. Users can filter by categories like beaches, temples, or waterfalls using scrollable chips."*

## 🔧 **Technical Implementation**

### **1. Automatic Location Detection**
```java
// Key code concept:
FusedLocationProviderClient → GPS coordinates
Geocoder → Convert coordinates to city name
Firebase query → Find attractions in that city
```

**How it works:**
1. App requests GPS permission
2. Gets current coordinates (latitude, longitude)
3. Uses Geocoder to convert coordinates to city name
4. Queries Firebase for attractions in that city

### **2. Category Filtering System**
```xml
<!-- Horizontal scrollable filters -->
<HorizontalScrollView>
    <ChipGroup app:singleSelection="true">
        <Chip android:text="All" android:checked="true" />
        <Chip android:text="Beach" />
        <Chip android:text="Temple" />
        <!-- More categories -->
    </ChipGroup>
</HorizontalScrollView>
```

**How it works:**
1. User taps category chip
2. App builds Firebase query with category filter
3. Results update in real-time
4. "All" category removes filter to show everything

### **3. Attraction Display**
- **Card Design**: Consistent AttractionCard style
- **Image Loading**: Glide library for smooth image loading
- **Placeholder Handling**: Special cards encourage contributions
- **Error Handling**: Graceful fallbacks for missing data

## 📱 **User Journey**

### **Step 1: App Launch**
- Splash screen appears
- Location permission requested
- GPS detection starts automatically

### **Step 2: Location Found**
- Toast shows detected city
- Attractions load automatically
- Filter chips become active

### **Step 3: Browsing**
- Scroll through attraction cards
- Tap filter chips to narrow results
- Tap attractions for details

### **Step 4: No Results**
- If no attractions found, shows "Help us grow" card
- Encourages users to add new locations
- Provides clear call-to-action

## 🎤 **Demo Script for Viva**

### **Opening** (30 seconds)
*"Let me show you the home discovery feature. When the app opens, it automatically detects my location..."*

### **Location Detection** (30 seconds)
1. Launch app
2. Point out location detection message
3. Show detected city in toast
4. Explain GPS → Geocoding → Database process

### **Filtering Demo** (45 seconds)
1. Show horizontal scrollable filters
2. Tap different categories
3. Point out real-time results updating
4. Explain Firebase query changes

### **Interaction Demo** (30 seconds)
1. Scroll through attraction cards
2. Point out consistent card design
3. Tap an attraction to show navigation
4. Explain card click handling

## 🏆 **Technical Highlights**

### **For Viva Questions:**

**Q: "How do you handle location permissions?"**
**A:** *"I check if permission is granted, request if needed, and provide fallback cities if user denies location access."*

**Q: "What if GPS fails?"**
**A:** *"I have multiple fallbacks - try different location providers, show default Sri Lankan cities, and provide manual refresh option."*

**Q: "How do filters work technically?"**
**A:** *"Each chip click triggers a new Firebase query. For 'All', I remove the category filter. For specific categories, I add a 'whereEqualTo' clause."*

**Q: "How do you ensure smooth performance?"**
**A:** *"I use Glide for efficient image loading, RecyclerView for smooth scrolling, and Firebase's built-in caching for offline support."*

## 🎨 **Design Decisions**

### **Why Horizontal Scroll for Filters?**
- Prevents wrapping to multiple lines
- Smooth gesture-based interaction
- Accommodates any number of categories

### **Why Automatic Location Detection?**
- Reduces user friction
- Provides immediate value
- Contextually relevant results

### **Why Card-Based Design?**
- Consistent with Material Design
- Easy to scan and interact with
- Scalable for different content types

## 📊 **Key Metrics for Success**
- Location detection success rate
- Filter usage statistics
- Card interaction rates
- User retention on home screen

This feature demonstrates core Android development skills: location services, real-time database integration, responsive UI design, and user experience optimization.
