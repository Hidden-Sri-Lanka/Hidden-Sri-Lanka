<!--toc-->
# Background Threding In Android App development 

## What is background Threding
In Android App Development the app UI run On a single main tread called UI Thread . if a long Running task runs on this Thread (HTTP request ,database quary,Hevy Computation) the UI will Freeze in this senario ,
- The Touch will not responsive 
- `Application Not Respondig` Error 

Can Happen .

So in android Development we need to Separate Thos Hevy Time Consiuming tasks to separate task that way UI will not be affected while those Processes are Running 

## Keay Points 
- UI Must stay Fast 
- Never Block UI Tread with Hevy Tasks 
- Run Network Calles in background/worker Threads 
- Only Main Thread can Update UI Views 
- In java You can use Thred excicuter Services ,Handler/Loopers

## What i Used In Hidden Sri Lanka Project 
### Image Loading is done with off the main Tread

I have expereanced that Google Photo Image Loding time is Very High 
Reson for that is i am using Google Photo Images and thay need to Process before Usable state so Glide is Performing,
    - image fetching 
    - Decoding
    - cashing
during that time UI is not responsive to Prevent that i have moved that to Background Thread  
The app is also Nomarlized google Photo image links befor Shering
- Where
    - LocationDetailActivity
    - GooglePhotosUrlHelper 
    - any list/detail screens with images

***Example :***
```java
// LocationDetailActivity.java
// Normalize Google Photos URL (e.g., convert shared link to direct image URL)
String normalizedUrl = GooglePhotosUrlHelper.normalize(originalUrl);

// Load image asynchronously (I/O and decoding off main thread)
Glide.with(this)
    .load(normalizedUrl)
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.image_error)
    .into(imageView);
```
### FireStore read /write activity using asynchronous call backs

In Fire store sdk methods are non-blocking .calls returen immidiatley  and listeners are involk on completion so ui remain responsive and updates are posted in listeners 

    - Where do i used 
        - HomeActivity
        - AddLocationActivity
        - LoginActivity/SignUpActivity (Authhentication)


```java
// HomeActivity.java
FirebaseFirestore db = FirebaseFirestore.getInstance();
db.collection("attractions")
  .get()
  .addOnSuccessListener(query -> {
      List<Attraction> items = query.toObjects(Attraction.class);
      // Update RecyclerView adapter on main thread
      attractionAdapter.submitList(items);
  })
  .addOnFailureListener(e -> {
      // Show error without blocking UI
      Toast.makeText(this, "Failed to load attractions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
  });
```

### Geocoding Done with worker thread with UI hand off 
since Geocoding is network-backed for some providers can block main thread so it is offloaded to background thread or exicuted and results are posted to UI thread

- Where Used 
    - LocationDetailActivity (smart fallback:- coordinates -> address -> name)
```java
// LocationDetailActivity.java
ExecutorService io = Executors.newSingleThreadExecutor();

io.execute(() -> {
    try {
        LatLng coords = geocodeAddress(address); // network/blocking work
        runOnUiThread(() -> {
            // Update map button state or show coordinates
            updateDirectionsButton(coords);
        });
    } catch (Exception e) {
        runOnUiThread(() -> {
            showGeocodingError(e.getMessage());
        });
    }
});
```
### derections Buttons 
in get derection button uses ,
- derect codinates 
-geocodede coordinates from address
- fallback to places search by name 

Each Steps are network depenent so thay run off the UI Thred Updates and Toasts are posted in Main Thread.

```java
// LocationDetailActivity.java
directionsButton.setOnClickListener(v -> {
    if (hasLatLng(attraction)) {
        openMapsWithLatLng(attraction.getLat(), attraction.getLng());
        return;
    }

    ExecutorService io = Executors.newSingleThreadExecutor();
    io.execute(() -> {
        LatLng coords = null;
        try {
            if (hasAddress(attraction)) {
                coords = geocodeAddress(attraction.getAddress());
            } else if (hasName(attraction)) {
                coords = geocodeByName(attraction.getName());
            }
        } catch (Exception ignored) {}

        LatLng finalCoords = coords;
        runOnUiThread(() -> {
            if (finalCoords != null) {
                openMapsWithLatLng(finalCoords.lat, finalCoords.lng);
            } else {
                openMapsWithQuery(attraction.getNameOrAddressFallback());
            }
        });
    });
});
```
### add Location Flow 
when adding location ,network input/output runs offthe main thread (firestore writes image checks ) ,Ui shows progress and handle completions orerrors via callbacks 

```java 
// AddLocationActivity.java
showSubmittingState(true);
db.collection("attractions").add(attraction)
  .addOnSuccessListener(docRef -> {
      showSubmittingState(false);
      Toast.makeText(this, "Location added", Toast.LENGTH_SHORT).show();
      finish();
  })
  .addOnFailureListener(e -> {
      showSubmittingState(false);
      showError("Submit failed: " + e.getMessage());
  });
```

### Resycler list
Since i am not using static images for my application i need to use recycler view so i can uses dynamic images and make one place holdere  and apply all incomming data fot that this way i can mak them smooth scroll

```java
// AttractionAdapter.java
@Override
public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Attraction item = getItem(position);
    holder.title.setText(item.getName());

    String normalizedUrl = GooglePhotosUrlHelper.normalize(item.getImageUrl());
    Glide.with(holder.itemView.getContext())
        .load(normalizedUrl)
        .placeholder(R.drawable.thumb_placeholder)
        .error(R.drawable.thumb_error)
        .into(holder.thumbnail);
}
```

___________________________________________________________________
Auther : Asitha Kanchna 
Date : 2025/08/18
Updated :





