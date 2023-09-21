package com.example.findmyway01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class pol_map extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private LatLng origin;
    private Marker selectedMarker = null;

    public ImageView icon;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LatLng destination;
    private OkHttpClient httpClient;
    private DatabaseReference databaseReference;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    FirebaseAuth auth;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pol_map);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Initialize the HTTP client
        httpClient = new OkHttpClient();

        // Initialize the Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //fetchAndDisplayAlertsFromFirebase();
    }


    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        // Set the initial camera position to Mandi Bahauddin's coordinates
        LatLng mandiBahauddin = new LatLng(32.5824296, 73.49112719999999);
        // 73.49112719999999, 32.5824296
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mandiBahauddin, 12.0f));

        if (isPoliceOrganizationUser()) {
            // Check for location permission
            if (checkPermissions()) {
                getLastLocation();
            } else {
                // Request location permission
                requestPermissions();
            }
        } else {
            Toast.makeText(this, "You are not part of the rescue organization.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (origin == null) {
            origin = latLng;
            mMap.addMarker(new MarkerOptions().position(origin).title("Origin"));
            Toast.makeText(getApplicationContext(), "Select destination", Toast.LENGTH_SHORT).show();
        } else if (destination == null) {
            destination = latLng;
            mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
            calculateAndDisplayOptimizedRoute();
        } else {
            mMap.clear();
            origin = null;
            destination = null;
        }
    }

    private void calculateAndDisplayOptimizedRoute() {
        if (origin != null && destination != null) {
            mMap.clear(); // Clear the map of any existing markers and polylines
            mMap.addMarker(new MarkerOptions().position(origin).title("Origin"));
            mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));

            String url = getDirectionsUrl(origin, destination);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseData = response.body().string();
                            List<LatLng> points = parseRouteData(responseData);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    displayOptimizedRoute(points);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Handle the non-successful response
                    }
                }
            });
        }
    }

    private boolean isPoliceOrganizationUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference userReference = databaseReference.child("officers").child(user.getUid());
            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String organization = dataSnapshot.child("org_1").getValue(String.class);
                        if ("police".equals(organization)) {
                            fetchAndDisplayAlertsFromFirebase();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the failure to fetch user data
                }
            });
        }

        return true;
    }

    private void fetchAndDisplayAlertsFromFirebase() {
        databaseReference.child("Alert").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<emergencies> alerts = new ArrayList<>();
                for (DataSnapshot alertSnapshot : dataSnapshot.getChildren()) {
                    String title = alertSnapshot.child("title").getValue(String.class);
                    String message = alertSnapshot.child("message").getValue(String.class);
                    double latitude = alertSnapshot.child("latitude").getValue(Double.class);
                    double longitude = alertSnapshot.child("longitude").getValue(Double.class);
                    emergencies alert = new emergencies(title, message, latitude, longitude);
                    alerts.add(alert);
                }

                displayAlertsOnMap(alerts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any error that occurred while fetching data
                Toast.makeText(pol_map.this, "Failed to fetch data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayAlertsOnMap(List<emergencies> alerts) {
        for (emergencies alert : alerts) {
            LatLng alertLocation = new LatLng(alert.getLatitude(), alert.getLongitude());
            mMap.addMarker(new MarkerOptions().position(alertLocation).title(alert.getTitle()));
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (selectedMarker != null) {
                    selectedMarker.remove(); // Remove the previously displayed marker
                }

                for (emergencies alert : alerts) {
                    LatLng alertLocation = new LatLng(alert.getLatitude(), alert.getLongitude());
                    if (marker.getPosition().equals(alertLocation)) {
                        selectedMarker = mMap.addMarker(new MarkerOptions().position(alertLocation).title(alert.getTitle()));
                        // Show the alert message in a dialog box and pass the marker's location
                        showAlertDialog(alert.getTitle(), alert.getMessage(), alertLocation);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void showAlertDialog(String title, String message, LatLng markerLocation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Set the clicked marker's location as the destination
                destination = markerLocation;
                mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
                calculateAndDisplayOptimizedRoute();
            }
        });
        builder.show();
    }

    private void setDestinationToCurrentLocation() {
        // Check for location permission
        if (checkPermissions()) {
            // Check if location is enabled
            if (isLocationEnabled()) {
                // Getting last location from FusedLocationClient object
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            destination = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(destination).title("Destination"));
                            calculateAndDisplayOptimizedRoute();
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // Request location permissions
            requestPermissions();
        }
    }
    private String getDirectionsUrl(LatLng origin, LatLng destination) {
        String apiKey = "AIzaSyDYfG5HAhngj0NdM8MPx4ioO6nzmXfT3cI";
        String apiUrl = "https://maps.googleapis.com/maps/api/directions/json?";
        String originParam = "origin=" + origin.latitude + "," + origin.longitude;
        String destinationParam = "destination=" + destination.latitude + "," + destination.longitude;
        String apiKeyParam = "key=" + apiKey;
        return apiUrl + originParam + "&" + destinationParam + "&" + apiKeyParam;
    }

    private List<LatLng> parseRouteData(String responseData) throws JSONException {
        List<LatLng> points = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(responseData);
        JSONArray routesArray = jsonObject.getJSONArray("routes");
        if (routesArray.length() > 0) {
            JSONObject routeObject = routesArray.getJSONObject(0);
            JSONObject overviewPolylineObject = routeObject.getJSONObject("overview_polyline");
            String encodedPoints = overviewPolylineObject.getString("points");
            points = decodePolyline(encodedPoints);
        }

        return points;
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
            poly.add(p);
        }

        return poly;
    }

    private void displayOptimizedRoute(List<LatLng> points) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(points)
                .color(Color.BLUE)
                .width(10);
        mMap.addPolyline(polylineOptions);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(origin);
        builder.include(destination);
        LatLngBounds bounds = builder.build();
        int padding = 100; // Adjust the padding as needed
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(DestinationCallback callback) {
        // Initializing LocationRequest object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location mLastLocation = locationResult.getLastLocation();
                double destLatitude = mLastLocation.getLatitude();
                double destLongitude = mLastLocation.getLongitude();

                // Pass the destination coordinates back to the callback
                callback.onDestinationReceived(destLatitude, destLongitude);
            }
        }, Looper.myLooper());
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {
            // check if location is enabled
            if (isLocationEnabled()) {
                // getting last location from FusedLocationClient object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData(new DestinationCallback() {
                                @Override
                                public void onDestinationReceived(double destLatitude, double destLongitude) {
                                    // This callback is used when the current location is received
                                    origin = new LatLng(destLatitude, destLongitude);
                                    mMap.addMarker(new MarkerOptions().position(origin).title("Origin"));
                                    Toast.makeText(getApplicationContext(), "Select destination", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            // Get the current location and set it as the origin
                            origin = new LatLng(location.getLatitude(), location.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(origin).title("Origin"));
                            Toast.makeText(getApplicationContext(), "Select destination", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available, request for permissions
            requestPermissions();
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    interface DestinationCallback {
        void onDestinationReceived(double destLatitude, double destLongitude);
    }
}