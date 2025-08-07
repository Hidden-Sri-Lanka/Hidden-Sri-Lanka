# Troubleshooting Guide

## Common Issues & Solutions

### 🔧 App Launch Issues

#### App Won't Start / Crashes on Launch
**Symptoms**: App closes immediately after opening or shows error screen

**Solutions**:
1. **Restart your device** - Simple reboot often fixes memory issues
2. **Clear app cache** - Go to Settings → Apps → Hidden Sri Lanka → Storage → Clear Cache
3. **Clear app data** - Settings → Apps → Hidden Sri Lanka → Storage → Clear Data (Note: This will log you out)
4. **Check available storage** - Ensure you have at least 100MB free space
5. **Update Android version** - App requires Android 5.0 or higher
6. **Reinstall the app** - Uninstall and download fresh copy

#### Slow App Performance
**Symptoms**: App takes long to load, screens freeze, animations stutter

**Solutions**:
1. **Close background apps** - Free up device memory
2. **Check internet speed** - Use Wi-Fi for better performance
3. **Restart the app** - Close completely and reopen
4. **Update the app** - Install latest version when available
5. **Check device specs** - App works best on devices with 2GB+ RAM

### 📍 Location Detection Problems

#### "Location Not Detected" Message
**Symptoms**: App shows default attractions instead of local ones

**Solutions**:
1. **Enable location services**:
   - Android: Settings → Location → Turn On
   - App-specific: Settings → Apps → Hidden Sri Lanka → Permissions → Location → Allow
2. **Check GPS signal**:
   - Go outside or near windows
   - Wait 30-60 seconds for GPS lock
   - Ensure location mode is set to "High accuracy"
3. **Refresh location**:
   - Use the search bar to manually refresh
   - Tap the refresh icon in toolbar
   - Close and reopen the app

#### Wrong Location Detected
**Symptoms**: App shows attractions for wrong city

**Solutions**:
1. **Manual city search** - Use search bar to enter correct city
2. **Wait for better GPS signal** - Location accuracy improves over time
3. **Check location settings** - Ensure "High accuracy" mode enabled
4. **Clear location cache** - Settings → Apps → Google Play Services → Storage → Clear Cache

#### Foreign Location Warning
**Symptoms**: "🌍 Foreign location detected" message appears

**This is normal when**:
- You're actually outside Sri Lanka
- Using VPN services
- GPS accuracy is poor

**Solutions**:
- Use manual search for Sri Lankan cities
- Disable VPN if using one
- Try common cities: Colombo, Kandy, Galle

### 🔐 Login & Account Issues

#### Can't Log In - "Invalid Credentials"
**Solutions**:
1. **Check username/password**:
   - Ensure correct spelling and capitalization
   - Check for extra spaces
   - Try typing instead of copy-pasting
2. **Password reset** (if feature available):
   - Contact support through About Us page
3. **Create new account**:
   - Use Sign Up if you can't remember credentials
   - Use different username if current one exists

#### "User Does Not Exist" Error
**Solutions**:
1. **Check username spelling** - Usernames are case-sensitive
2. **Try signing up** - You may not have created account yet
3. **Contact support** - If you're sure account exists

#### Account Creation Fails
**Common issues**:
- **Username already exists** - Try a different username
- **Invalid email format** - Ensure proper email format (user@domain.com)
- **Password too short** - Use at least 6 characters
- **Network error** - Check internet connection and retry

### 🌐 Network & Data Issues

#### "No Internet Connection" / Data Won't Load
**Symptoms**: Attractions won't load, error messages about connectivity

**Solutions**:
1. **Check internet connection**:
   - Try opening a webpage in browser
   - Switch between Wi-Fi and mobile data
   - Move to area with better signal
2. **Check app permissions**:
   - Ensure app can access internet
   - Check data usage restrictions
3. **Clear app cache**:
   - Settings → Apps → Hidden Sri Lanka → Storage → Clear Cache
4. **Try different network**:
   - Switch to Wi-Fi if on mobile data
   - Try different Wi-Fi network

#### Images Won't Load
**Symptoms**: Attraction cards show placeholder images or broken image icons

**Solutions**:
1. **Check internet speed** - Images need good connection
2. **Wait longer** - Large images take time to load
3. **Restart app** - Fresh start often fixes image loading
4. **Clear image cache** - Clear app cache in device settings

#### Firestore/Database Errors
**Symptoms**: "Firebase Database Access Denied" or similar error messages

**Solutions**:
1. **Check internet connection** - Database needs connectivity
2. **Wait and retry** - Temporary server issues may resolve
3. **Update app** - Database configuration may have changed
4. **Contact support** - May indicate server-side issues

### 🔍 Search & Filter Issues

#### Search Doesn't Work
**Symptoms**: Typing in search bar shows no results or errors

**Solutions**:
1. **Check spelling** - Try common Sri Lankan cities (Colombo, Kandy, Galle)
2. **Use English names** - App currently uses English city names
3. **Try partial search** - Type first few letters and see suggestions
4. **Clear search field** - Remove all text and try again

#### Filters Show No Results
**Symptoms**: Selecting category filters shows "No attractions found"

**Solutions**:
1. **Try "All" filter** - See if any attractions exist for the city
2. **Search different city** - Current location may have limited data
3. **Contribute data** - Add attractions for your area using "Add Location"
4. **Check internet** - Ensure data can load properly

#### "Help Us Grow Our Database" Message
**This appears when**: No attractions exist for your current location

**What to do**:
1. **This is normal** - We're still building the database
2. **Search major cities** - Try Colombo, Kandy, Galle, Anuradhapura
3. **Add local attractions** - Use "Add Location" to contribute
4. **Try nearby areas** - Search neighboring cities or towns

### 📝 Add Location Form Issues

#### Form Won't Submit
**Common problems**:
1. **Required fields missing** - Check all fields marked with *
2. **Invalid image URL** - Ensure URL is accessible and points to image
3. **Description too short** - Write at least 50 characters
4. **Network timeout** - Check internet and retry

#### Image Preview Not Working
**Solutions**:
1. **Check image URL format** - Should end with .jpg, .png, etc.
2. **Test URL in browser** - Open URL separately to verify it works
3. **Use different hosting service** - Try Imgur or Google Photos
4. **Check image size** - Very large images may not load

#### Can't Select Province/City
**Solutions**:
1. **Select province first** - Cities populate based on province selection
2. **Scroll through options** - Lists may be long
3. **Check spelling** - Look for similar names
4. **Restart form** - Clear and start over if stuck

### 📱 Device-Specific Issues

#### Small Screen Display Problems
**Solutions**:
1. **Rotate device** - Try landscape mode for better visibility
2. **Adjust font size** - Use device accessibility settings
3. **Scroll horizontally** - Filter chips scroll left/right
4. **Zoom interface** - Use device accessibility zoom

#### Older Android Versions
**Minimum requirements**: Android 5.0 (API 21)

**If using older version**:
- Update Android OS if possible
- Some features may not work properly
- Consider upgrading device

#### Memory Issues
**Symptoms**: App closes unexpectedly, slow performance

**Solutions**:
1. **Close other apps** - Free up memory
2. **Restart device** - Clear memory completely
3. **Check storage space** - Keep at least 1GB free
4. **Reduce image quality** in device camera settings

### 🔄 Session & Logout Issues

#### Automatically Logged Out
**Possible causes**:
- App updated/reinstalled
- Device storage cleared
- Session expired

**Solutions**:
1. **Log in again** - Enter your credentials
2. **Check "Remember me"** option if available
3. **Contact support** - If frequent logout issues occur

#### Can't Log Out
**Solutions**:
1. **Use menu logout** - Drawer → Log Out
2. **Clear app data** - Settings → Apps → Hidden Sri Lanka → Storage → Clear Data
3. **Reinstall app** - Complete fresh start

## Getting Additional Help

### When to Contact Support
- Persistent crashes after trying all solutions
- Data loss or corruption issues
- Account access problems that can't be resolved
- Suspected bugs or app malfunctions

### How to Contact Support
1. **About Us page** - Find contact information in app
2. **GitHub issues** - Report technical problems
3. **Email support** - Include device model, Android version, and problem description

### Information to Include When Reporting Issues
- **Device model** (e.g., Samsung Galaxy S21)
- **Android version** (e.g., Android 12)
- **App version** (found in About Us)
- **Exact error message** (screenshot if possible)
- **Steps to reproduce** the problem
- **When it started happening**

### Debug Information Collection
To help us solve your problem:
1. **Take screenshots** of error messages
2. **Note exact steps** that cause the issue
3. **Try on different network** (Wi-Fi vs mobile data)
4. **Test with different user account** if possible

---

*Most issues can be resolved with these solutions. If problems persist, don't hesitate to reach out for additional support through the app's About Us section.*
