package com.example.findmyway01;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

//import com.google.android.gms.location.LocationRequest;

public class commu_map extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private LatLng origin;
    public ImageView icon;
  //  private DBHelper db;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LatLng destination;
    private OkHttpClient httpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commu_map);

        // Initialize the HTTP client
        httpClient = new OkHttpClient();
   //     db = new DBHelper(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        // Set the initial camera position to Mandi Bahauddin's coordinates
        LatLng mandiBahauddin = new LatLng(32.5824296, 73.49112719999999);
        // 73.49112719999999, 32.5824296
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mandiBahauddin, 12.0f));
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
            // Clear existing markers and route
            mMap.clear();
            origin = null;
            destination = null;
        }
    }

    //
    private void calculateAndDisplayOptimizedRoute() {
        if (origin != null && destination != null) {
            String url = getDirectionsUrl(origin, destination);

            // Make a request to the Directions API
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    // Handle the request failure
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String responseData = response.body().string();
                            // Parse the response data and extract the route information
                            List<LatLng> points = parseRouteData(responseData);

                            // Update the UI on the main thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Display the optimized route on the map
                                    displayOptimizedRoute(points);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle the JSON parsing error
                        }
                    } else {
                        // Handle the non-successful response
                    }
                }
            });
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
}
