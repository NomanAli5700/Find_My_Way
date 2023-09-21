package com.example.findmyway01;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class res_sub extends AppCompatActivity {
    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    FirebaseAuth auth;
    private DatabaseReference mDatabaseRef;
    public int c;
    //public String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_sub);

        latitudeTextView = findViewById(R.id.lat);
        longitTextView = findViewById(R.id.lan);

        ImageView med, acc, fire, earth;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

        med = findViewById(R.id.med);
        acc = findViewById(R.id.acc);
        fire = findViewById(R.id.fire);
        earth = findViewById(R.id.earth);
        auth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("emergencies");

        med.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call getLastLocation() with an OnSuccessListener to get the location
                getLastLocation();
               c=1;
                Intent it = new Intent(res_sub.this, map.class);
          //      it.putExtra("message", "Hi! Need your help, I have a medical emergency!!");
                Toast.makeText(res_sub.this, "String putted.", Toast.LENGTH_LONG).show();
                startActivity(it);
            }
        });

        acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
                c=2;
                Intent it = new Intent(res_sub.this, map.class);
            //    it.putExtra("message", "I need help in a car accident!");
                Toast.makeText(res_sub.this, "String putted.", Toast.LENGTH_LONG).show();
                startActivity(it);
            }
        });

        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
                c=3;
                Intent it = new Intent(res_sub.this, map.class);
              //  it.putExtra("message", "There's a fire emergency, please help!");
                Toast.makeText(res_sub.this, "String putted.", Toast.LENGTH_LONG).show();
                startActivity(it);
            }
        });

        earth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
                c=4;
                Intent it = new Intent(res_sub.this, map.class);
                //it.putExtra( "message", "I need rescue from an earthquake!");
                Toast.makeText(res_sub.this, "String putted.", Toast.LENGTH_LONG).show();
                startActivity(it);
            }
        });
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
                            requestNewLocationData();
                        } else {
                            latitudeTextView.setText(location.getLatitude() + "");
                            longitTextView.setText(location.getLongitude() + "");
                            // Retrieve the alert message from the Intent
                            //String message = getIntent().getExtras().getString("message");
                            String message = null;
                            //if (message == null || message.isEmpty()) {
                             //   Toast.makeText(res_sub.this, "Emergency message is empty.", Toast.LENGTH_LONG).show();
                           // } else {
                               // // Save the location and emergency alert message to Firebase
                             //   uploadDataToFirebase("Help Rescue Officer", message, location.getLatitude(), location.getLongitude());
                           // }
                            if (c == 1) {
                                message = "Hi! Need your help, I have a medical emergency!!";
                                Toast.makeText(res_sub.this, "Emergency message is empty.", Toast.LENGTH_LONG).show();
                            }else if (c == 2){
                                message = "Hi! Need your help,I need help in a car accident!";
                            }else if (c == 3) {
                                message = "Hi! Need your help, I have a fire incident!!";
                                Toast.makeText(res_sub.this, "Emergency message is empty.", Toast.LENGTH_LONG).show();
                            }else if (c == 4){
                                message = "Hi! Need your help, I need rescue from an earthquake!";
                            }
                              
                                // Save the location and emergency alert message to Firebase
                                uploadDataToFirebase("Help Rescue Officer", message, location.getLatitude(), location.getLongitude());
                            
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

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        // Initializing LocationRequest object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            // Retrieve the alert message from the Intent
           // Intent intent = getIntent();

          //  String message = getIntent().getExtras().getString("message");
            Toast.makeText(res_sub.this, "String got.", Toast.LENGTH_LONG).show();
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");

            String message;
            if (c == 1) {
                message = "Hi! Need your help, I have a medical emergency!!";
                Toast.makeText(res_sub.this, "Emergency message is empty.", Toast.LENGTH_LONG).show();
            }else if (c == 2){
                message = "Hi! Need your help,I need help in a car accident!";
            }else if (c == 3) {
                message = "Hi! Need your help, I have a fire incident!!";
                Toast.makeText(res_sub.this, "Emergency message is empty.", Toast.LENGTH_LONG).show();
            }else {
                message = "Hi! Need your help, I need rescue from an earthquake!";
            }

            // Save the location and emergency alert message to Firebase
            uploadDataToFirebase("Help Rescue Officer", message, mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void uploadDataToFirebase(String title, String message, double latitude, double longitude) {
        emergencies emergencyData = new emergencies(title, message, latitude, longitude);

        // Generate a unique key for each entry
        String key = mDatabaseRef.push().getKey();

        // Store the data in Firebase using the generated key
        mDatabaseRef.child(key).setValue(emergencyData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(res_sub.this, "Location and emergency alert sent!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(res_sub.this, "Failed to send location and emergency alert: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
