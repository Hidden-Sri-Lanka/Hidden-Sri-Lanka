# API Reference

## Firebase Collections & Data Models

### Collections Structure

```
/cities/{cityName}/attractions/{attractionId}
```

### Data Models

#### Attraction Model
```javascript
{
  "name": string,                    // Required - Attraction name
  "category": string,                // Required - Category type
  "description": string,             // Required - Detailed description
  "images": array<string>,           // Required - Array of image URLs
  "youtubeUrl": string,             // Optional - YouTube video URL
  "contributorName": string,         // Required - Contributor name
  "contributedAt": timestamp,        // Required - Contribution timestamp
  "province": string,               // Required - Sri Lankan province
  "city": string,                   // Required - City name
  "latitude": number,               // Optional - GPS latitude
  "longitude": number,              // Optional - GPS longitude
  "documentId": string,             // Auto-generated - Firestore document ID
  "isPlaceholder": boolean          // Optional - Placeholder content flag
}
```

#### User Model (Realtime Database)
```javascript
{
  "username": string,               // Required - Unique username
  "email": string,                  // Required - Email address
  "name": string,                   // Required - Display name
  "password": string,               // Required - Encrypted password
  "createdAt": timestamp           // Optional - Account creation time
}
```

## Categories

### Supported Categories
- `"Historical Site"` - Archaeological sites, heritage buildings
- `"WaterFall"` - Natural waterfalls and water features
- `"Beach"` - Coastal attractions and beaches
- `"Mountain"` - Hill country locations and viewpoints
- `"Temple"` - Religious sites and temples
- `"National Park"` - Wildlife reserves and nature parks
- `"More"` - Additional miscellaneous categories

### Sri Lankan Provinces
- `"Western Province"`
- `"Central Province"`
- `"Southern Province"`
- `"Northern Province"`
- `"Eastern Province"`
- `"North Western Province"`
- `"North Central Province"`
- `"Uva Province"`
- `"Sabaragamuwa Province"`

## Firebase Queries

### Get All Attractions for City
```javascript
db.collection("cities")
  .document(cityName)
  .collection("attractions")
  .get()
```

### Get Attractions by Category
```javascript
db.collection("cities")
  .document(cityName)
  .collection("attractions")
  .whereEqualTo("category", categoryName)
  .get()
```

### Add New Attraction
```javascript
db.collection("cities")
  .document(cityName)
  .collection("attractions")
  .add(attractionData)
```

## Authentication Endpoints

### User Registration
**Collection**: `users` (Realtime Database)
**Method**: `POST`
**Data**:
```javascript
{
  "username": "unique_username",
  "email": "user@example.com",
  "name": "User Name",
  "password": "hashed_password"
}
```

### User Login
**Collection**: `users` (Realtime Database)
**Method**: `GET`
**Query**: `orderByChild("username").equalTo(username)`

## Error Codes

### Firebase Errors
- `PERMISSION_DENIED` - Insufficient permissions
- `NOT_FOUND` - Document/collection not found
- `CANCELLED` - Operation cancelled
- `UNKNOWN` - Unknown error occurred
- `INVALID_ARGUMENT` - Invalid query parameters
- `DEADLINE_EXCEEDED` - Request timeout
- `ALREADY_EXISTS` - Document already exists
- `RESOURCE_EXHAUSTED` - Quota exceeded
- `FAILED_PRECONDITION` - Operation failed precondition
- `ABORTED` - Operation aborted
- `OUT_OF_RANGE` - Out of valid range
- `UNIMPLEMENTED` - Operation not implemented
- `INTERNAL` - Internal server error
- `UNAVAILABLE` - Service unavailable
- `DATA_LOSS` - Unrecoverable data loss

### Custom Error Handling
```java
public class ApiErrorHandler {
    public static void handleFirebaseError(Exception e) {
        if (e.getMessage().contains("PERMISSION_DENIED")) {
            showError("Database access denied. Please check permissions.");
        } else if (e.getMessage().contains("UNAVAILABLE")) {
            showError("Service temporarily unavailable. Please try again.");
        } else {
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }
}
```

## Rate Limits & Quotas

### Firestore Limits
- **Reads**: 50,000 per day (free tier)
- **Writes**: 20,000 per day (free tier)
- **Deletes**: 20,000 per day (free tier)
- **Document Size**: 1 MB maximum
- **Collection Depth**: 100 levels maximum

### Realtime Database Limits
- **Simultaneous Connections**: 100 (free tier)
- **GB Stored**: 1 GB (free tier)
- **GB Downloaded**: 10 GB/month (free tier)

## Security Rules

### Firestore Rules
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /cities/{city}/attractions/{attraction} {
      allow read: if true;
      allow write: if request.auth != null && 
                      resource == null && 
                      isValidAttraction(request.resource.data);
    }
  }
  
  function isValidAttraction(data) {
    return data.keys().hasAll(['name', 'category', 'description', 'contributorName']) &&
           data.name is string && data.name.size() > 0 &&
           data.category in ['Historical Site', 'WaterFall', 'Beach', 'Mountain', 'Temple', 'National Park', 'More'] &&
           data.description is string && data.description.size() >= 50;
  }
}
```

### Realtime Database Rules
```javascript
{
  "rules": {
    "users": {
      ".read": true,
      ".write": true,
      "$userId": {
        ".validate": "newData.hasChildren(['username', 'email', 'name', 'password'])"
      }
    }
  }
}
```

## Best Practices

### Query Optimization
1. **Use Indexes**: Create composite indexes for common queries
2. **Limit Results**: Use `.limit()` to prevent large data transfers
3. **Cache Results**: Implement local caching for frequently accessed data
4. **Batch Operations**: Group multiple operations when possible

### Error Handling
1. **Graceful Degradation**: Provide fallback content when APIs fail
2. **Retry Logic**: Implement exponential backoff for failed requests
3. **User Feedback**: Show meaningful error messages to users
4. **Logging**: Log errors for debugging and monitoring

### Security Considerations
1. **Validate Input**: Sanitize all user inputs before database operations
2. **Use Security Rules**: Implement proper Firebase security rules
3. **Minimize Permissions**: Grant least privilege necessary
4. **Monitor Usage**: Track API usage and detect anomalies

---
*This API reference provides comprehensive information for integrating with the Hidden Sri Lanka backend services.*
