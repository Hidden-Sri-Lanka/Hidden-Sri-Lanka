# App Architecture

## Overview
Hidden Sri Lanka follows Android best practices with a modular, maintainable architecture that supports scalability and testability.

## Architecture Pattern

### Model-View-Controller (MVC) with BaseActivity Pattern
The app uses a modified MVC pattern with a BaseActivity that provides common functionality across all screens.

```
┌─────────────────────────────────────────┐
│                View Layer               │
│  Activities, Layouts, Adapters         │
├─────────────────────────────────────────┤
│              Controller Layer           │
│  Activity Logic, Event Handlers        │
├─────────────────────────────────────────┤
│                Model Layer              │
│  Data Classes, Firebase Integration     │
└─────────────────────────────────────────┘
```

## Core Components

### 1. BaseActivity Pattern
```java
public abstract class BaseActivity extends AppCompatActivity {
    protected ActivityBaseBinding binding;
    protected SessionManager sessionManager;
    
    protected abstract int getLayoutResourceId();
    protected abstract String getActivityTitle();
}
```

**Benefits**:
- Consistent navigation across all screens
- Shared session management
- Common toolbar and drawer functionality
- Reduced code duplication

### 2. Session Management
```java
public class SessionManager {
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    
    // Session lifecycle management
    public void createLoginSession(String username, String email, String name);
    public boolean isLoggedIn();
    public void logoutUser();
}
```

**Features**:
- Persistent user sessions
- Automatic login state checking
- Secure session cleanup
- Multi-field user data storage

### 3. Data Models
```java
public class Attraction {
    private String name;
    private String category;
    private String description;
    private List<String> images;
    private String youtubeUrl;
    private String contributorName;
    private long contributedAt;
    private double latitude;
    private double longitude;
}
```

## Application Flow

### 1. App Launch Sequence
```
MainActivity (Splash)
    ↓
SessionManager.isLoggedIn()
    ├── true → HomeActivity
    └── false → WelcomeActivity
                    ↓
                LoginActivity/SignUpActivity
                    ↓
                HomeActivity
```

### 2. Authentication Flow
```
User Input → Validation → Firebase Query → Session Creation → Navigation
```

### 3. Data Flow
```
User Action → Firebase Query → Data Processing → UI Update → User Feedback
```

## Package Structure

```
com.s23010526.hiddensrilanka/
├── models/
│   ├── Attraction.java
│   ├── User.java
│   └── Location.java
├── activities/
│   ├── BaseActivity.java
│   ├── MainActivity.java
│   ├── HomeActivity.java
│   ├── LoginActivity.java
│   ├── SignUpActivity.java
│   ├── AddLocationActivity.java
│   ├── SettingsActivity.java
│   └── AboutUsActivity.java
├── adapters/
│   └── AttractionAdapter.java
├── utils/
│   ├── SessionManager.java
│   └── DataSeeder.java
└── databinding/
    └── Generated binding classes
```

## Design Patterns Used

### 1. Singleton Pattern
```java
// SessionManager implementation
public class SessionManager {
    private static SessionManager instance;
    
    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }
}
```

### 2. Adapter Pattern
```java
public class AttractionAdapter extends RecyclerView.Adapter<AttractionAdapter.ViewHolder> {
    private List<Attraction> attractions;
    
    // Bridge between data and RecyclerView
}
```

### 3. Template Method Pattern
```java
// BaseActivity defines template, subclasses implement specifics
public abstract class BaseActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Common setup
        setupCustomImplementation(); // Template method
    }
    
    protected abstract void setupCustomImplementation();
}
```

## Data Management

### 1. Local Storage
- **SharedPreferences**: User sessions and preferences
- **Room Database**: Future enhancement for offline data
- **File Storage**: Cached images and temporary data

### 2. Remote Storage
- **Firebase Firestore**: Attraction data and metadata
- **Firebase Realtime Database**: User authentication
- **Firebase Storage**: Image hosting (future enhancement)

### 3. Caching Strategy
```java
// Multi-level caching
Memory Cache → Local Storage → Remote Database
```

## UI Architecture

### 1. Layout Hierarchy
```xml
BaseActivity Layout:
├── DrawerLayout
│   ├── LinearLayout (Main Content)
│   │   ├── Toolbar (Custom)
│   │   └── FrameLayout (Content)
│   └── NavigationView (Drawer)
```

### 2. View Binding
```java
// Type-safe view references
protected ActivityBaseBinding binding;
binding = ActivityBaseBinding.inflate(getLayoutInflater());
setContentView(binding.getRoot());
```

### 3. Material Design
- **Material Components**: Buttons, text fields, cards
- **Material Theming**: Consistent color and typography
- **Material Motion**: Smooth animations and transitions

## Error Handling

### 1. Exception Hierarchy
```
AppException
├── NetworkException
├── AuthenticationException
├── ValidationException
└── DatabaseException
```

### 2. Error Recovery
```java
try {
    // Risky operation
} catch (NetworkException e) {
    // Show cached data
} catch (DatabaseException e) {
    // Retry mechanism
} catch (Exception e) {
    // Generic error handling
}
```

## Testing Architecture

### 1. Unit Testing
```java
@Test
public void testSessionManager() {
    SessionManager manager = new SessionManager(context);
    manager.createLoginSession("user", "email", "name");
    assertTrue(manager.isLoggedIn());
}
```

### 2. Integration Testing
```java
@Test
public void testFirebaseIntegration() {
    // Test Firebase queries and responses
}
```

### 3. UI Testing
```java
@Test
public void testLoginFlow() {
    // Test complete login user journey
}
```

## Performance Considerations

### 1. Memory Management
- **View Recycling**: RecyclerView for efficient scrolling
- **Image Loading**: Glide for optimized image handling
- **Lifecycle Awareness**: Proper cleanup in onDestroy()

### 2. Network Optimization
- **Request Batching**: Group related Firebase queries
- **Caching**: Minimize redundant network calls
- **Compression**: Optimize image sizes and formats

### 3. UI Performance
- **Async Operations**: Background threads for heavy tasks
- **Lazy Loading**: Load content as needed
- **Smooth Animations**: 60fps target for all animations

## Security Architecture

### 1. Data Protection
- **Input Validation**: Sanitize all user inputs
- **SQL Injection Prevention**: Use parameterized queries
- **XSS Protection**: Validate URLs and content

### 2. Authentication Security
- **Session Management**: Secure token handling
- **Password Security**: Hash and salt passwords
- **Permission Checking**: Validate user permissions

### 3. Network Security
- **HTTPS Only**: Secure data transmission
- **Certificate Pinning**: Prevent man-in-the-middle attacks
- **API Key Protection**: Secure Firebase configuration

## Scalability Considerations

### 1. Code Scalability
- **Modular Design**: Easy to add new features
- **Interface Segregation**: Small, focused interfaces
- **Dependency Injection**: Loose coupling between components

### 2. Data Scalability
- **Database Indexing**: Optimize query performance
- **Data Pagination**: Handle large datasets efficiently
- **Caching Strategy**: Reduce database load

### 3. Performance Scalability
- **Lazy Loading**: Load features on demand
- **Resource Optimization**: Minimize memory and CPU usage
- **Background Processing**: Handle heavy tasks efficiently

---
*This architecture provides a solid foundation for the Hidden Sri Lanka app with excellent maintainability, scalability, and performance characteristics.*
