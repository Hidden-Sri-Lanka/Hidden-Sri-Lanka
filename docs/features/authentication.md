# User Authentication System

## Overview
The Hidden Sri Lanka app implements a robust authentication system with persistent login functionality using Firebase Realtime Database and local session management.

## Features

### 🔐 Login System
- **Email/Username Login**: Users can log in using their registered credentials
- **Real-time Validation**: Form validation with immediate feedback
- **Secure Authentication**: Password verification against Firebase database
- **Session Persistence**: Login state maintained using SharedPreferences

### 📝 Registration System
- **User Registration**: New users can create accounts with username, email, and password
- **Data Validation**: Comprehensive input validation including:
  - Username uniqueness check
  - Email format validation
  - Password strength requirements
  - Confirmation password matching
- **Firebase Integration**: User data stored securely in Firebase Realtime Database

### 🔄 Session Management
- **Persistent Login**: Users stay logged in between app sessions
- **Automatic Redirect**: Logged-in users bypass login screen on app startup
- **Secure Logout**: Complete session cleanup and redirect to welcome screen

## Technical Implementation

### Architecture Components
```
SessionManager.java - Handles local session storage
LoginActivity.java - Login interface and validation
SignUpActivity.java - Registration interface and validation
MainActivity.java - Entry point with authentication check
```

### Session Storage
- **SharedPreferences**: Local storage for user session data
- **Stored Data**: Username, email, name, login status
- **Security**: Session data cleared on logout

### Authentication Flow
1. **App Launch**: MainActivity checks existing session
2. **Session Found**: Direct redirect to HomeActivity
3. **No Session**: Redirect to WelcomeActivity → Login/SignUp
4. **Successful Login**: Session created → HomeActivity
5. **Logout**: Session cleared → WelcomeActivity

## User Interface

### Login Screen
- Clean, intuitive design with app branding
- Username and password input fields
- Login button with loading states
- Redirect to signup option
- Form validation with error messages

### Registration Screen
- Comprehensive form with all required fields
- Real-time validation feedback
- Password confirmation
- Redirect to login after successful registration

### Session States
- **Logged In**: Access to all app features
- **Logged Out**: Limited to welcome, login, and signup screens

## Error Handling
- Network connectivity issues
- Invalid credentials feedback
- Registration validation errors
- Database connection errors

## Security Features
- Password encryption (handled by Firebase)
- Session timeout (configurable)
- Input sanitization
- SQL injection prevention (Firebase security)

## Usage Examples

### Checking Login Status
```java
SessionManager sessionManager = new SessionManager(this);
if (sessionManager.isLoggedIn()) {
    // User is logged in
    String username = sessionManager.getUsername();
}
```

### Creating Session
```java
sessionManager.createLoginSession(username, email, name);
```

### Logout
```java
sessionManager.logoutUser();
// Redirect to welcome screen
```

---
*This system ensures a smooth, secure user experience with minimal friction for returning users.*
