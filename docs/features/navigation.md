# Navigation System

## Overview
The Hidden Sri Lanka app uses a drawer-based navigation system with a custom toolbar, providing intuitive access to all app features while maintaining a clean, modern interface.

## Architecture

### Navigation Components
- **BaseActivity**: Abstract base class for consistent navigation
- **Drawer Layout**: Side navigation menu for main features
- **Custom Toolbar**: Integrated search and branding
- **Material Navigation**: Google Material Design components

### Navigation Hierarchy
```
MainActivity (Splash) → WelcomeActivity → LoginActivity → HomeActivity (Base)
                                     ↘ SignUpActivity ↗
```

## Drawer Navigation Menu

### Primary Navigation Items
- **🏠 Home**: Main attraction discovery screen
- **⚙️ Settings**: App preferences and configuration
- **🗺️ Explore Map**: Full-screen map view of attractions
- **➕ Add Location**: Community contribution form
- **❤️ Favorites**: Saved attractions (Coming Soon)
- **ℹ️ About Us**: App information and credits
- **👤 Profile**: User profile management (Coming Soon)
- **🚪 Log Out**: Session termination and return to welcome

### Navigation States
- **Active Item**: Highlighted current screen
- **Inactive Items**: Available navigation options
- **Disabled Items**: Future features with "Coming Soon" status
- **Logout**: Special handling with session cleanup

## Custom Toolbar

### Toolbar Components
- **App Title**: Dynamic title based on current screen
- **Search Field**: Integrated attraction search
- **Search Icon**: Interactive search trigger
- **Hamburger Menu**: Drawer toggle icon

### Search Integration
- **Real-time Search**: Instant attraction filtering
- **City Override**: Manual city selection
- **Auto-complete**: Search suggestions
- **Clear Function**: Easy search reset

### Responsive Design
- **Title Truncation**: Handle long screen titles
- **Search Field Sizing**: Adaptive to screen size
- **Touch Targets**: Accessible button sizes
- **Visual Hierarchy**: Clear information priority

## Technical Implementation

### BaseActivity Pattern
```java
public abstract class BaseActivity extends AppCompatActivity {
    protected ActivityBaseBinding binding;
    protected SessionManager sessionManager;
    
    protected abstract int getLayoutResourceId();
    protected abstract String getActivityTitle();
}
```

### Navigation Handling
```java
@Override
public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int itemId = item.getItemId();
    Intent intent = null;
    
    // Handle navigation logic
    // Prevent navigation to same screen
    // Clear activity stack for logout
    
    if (intent != null) {
        startActivity(intent);
    }
    
    binding.drawerLayout.closeDrawer(GravityCompat.START);
    return true;
}
```

### Session-Aware Navigation
- **Login Check**: Automatic redirection for unauthenticated users
- **State Preservation**: Maintain navigation state across sessions
- **Logout Handling**: Complete session cleanup and navigation reset

## Screen-Specific Features

### Home Screen
- **Location Display**: Current city in title
- **Filter Integration**: Category filters below toolbar
- **Refresh Action**: Pull-to-refresh and manual refresh
- **Search Override**: City-specific attraction search

### Settings Screen
- **Theme Toggle**: Dark/light mode switching (Coming Soon)
- **Preferences**: User customization options
- **Account Settings**: Profile management integration
- **App Information**: Version and update details

### Add Location Screen
- **Form Navigation**: Step-by-step form completion
- **Save Draft**: Preserve form data during navigation
- **Success Routing**: Post-submission navigation
- **Cancel Handling**: Safe form exit with confirmation

### About Us Screen
- **Static Content**: App information and team credits
- **External Links**: Social media and contact information
- **Legal Pages**: Privacy policy and terms of service
- **Version Information**: App build and update details

## Navigation Flows

### Authentication Flow
```
Splash → Welcome → Login → Home
       ↘ (if logged in) → Home
```

### Main App Flow
```
Home ←→ Settings
 ↕       ↕
Add Location ←→ About Us
 ↕       ↕
Map View ←→ Profile (Coming Soon)
```

### Logout Flow
```
Any Screen → Logout → Session Clear → Welcome
```

## User Experience

### Navigation Feedback
- **Visual Indicators**: Active screen highlighting
- **Loading States**: Progress during screen transitions
- **Error Handling**: Graceful failure recovery
- **Toast Messages**: Confirmation of navigation actions

### Accessibility
- **Screen Reader**: Proper navigation announcements
- **Keyboard Navigation**: Full keyboard accessibility
- **Focus Management**: Logical tab order
- **Touch Targets**: Minimum 48dp interactive areas

### Performance
- **Lazy Loading**: Load screens on demand
- **Memory Management**: Proper activity lifecycle handling
- **Animation**: Smooth transitions between screens
- **Back Stack**: Efficient navigation history management

## Customization Features

### Theming
- **Material Design**: Consistent visual language
- **Brand Colors**: Hidden Sri Lanka color scheme
- **Typography**: Custom font families (Cursive)
- **Dark Mode**: Planned future enhancement

### Localization Ready
- **String Resources**: Externalized text content
- **RTL Support**: Right-to-left language preparation
- **Cultural Adaptation**: Sri Lankan context optimization
- **Multi-language**: Framework for future translations

## Error Handling

### Navigation Errors
- **Missing Permissions**: Graceful permission handling
- **Network Issues**: Offline navigation capabilities
- **Invalid States**: Recovery from corrupted navigation state
- **Memory Issues**: Low memory navigation optimization

### Recovery Mechanisms
- **Automatic Retry**: Retry failed navigation attempts
- **Fallback Options**: Alternative navigation paths
- **User Guidance**: Clear error messages and next steps
- **Debug Information**: Development-time navigation debugging

## Future Enhancements

### Planned Features
- **Bottom Navigation**: Alternative navigation pattern
- **Tab Navigation**: Secondary navigation within screens
- **Deep Linking**: Direct navigation to specific content
- **Voice Navigation**: Accessibility enhancement

### Technical Improvements
- **Navigation Component**: Android Jetpack Navigation
- **Single Activity**: Modern navigation architecture
- **Gesture Navigation**: Swipe-based navigation
- **Progressive Web App**: Web-based navigation support

---
*The navigation system provides users with intuitive, accessible access to all app features while maintaining excellent performance and user experience.*
