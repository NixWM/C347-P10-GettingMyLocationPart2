package sg.edu.ro.c346.id16046530.c347_p10_gettingmylocationpart2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {
    Button btnStartDetector, btnStopDetector, btnShowRecords;
    TextView tvLatitude, tvLongitude;
    ToggleButton tbMusic;
    private GoogleMap map;
    FusedLocationProviderClient client;
//    LocationRequest mLocationRequest;
//    LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnShowRecords = findViewById(R.id.btnCheckRecords);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tbMusic = findViewById(R.id.toggleMusic);
        btnStartDetector = findViewById(R.id.btnStartDetector);
        btnStopDetector = findViewById(R.id.btnStopDetector);


        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);

//        String folderLocation = getFilesDir().getAbsolutePath() + "/Folder";
//        File folder = new File(folderLocation);
//        if(folder.exists() == false) {
//            boolean result = folder.mkdir();
//            if(result == true) {
//                Log.d("File Read/ Write", "Folder Created");
//            } else {
//                Log.d("File Read/ Write", "Folder Not Created");
//            }
//        }

        // Map Async
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                UiSettings ui = map.getUiSettings();
                ui.setZoomControlsEnabled(true);
                ui.setCompassEnabled(true);


                if (checkPermission() == true) {
                    Task<Location> task = client.getLastLocation();
                    task.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                tvLatitude.setText("Latitude: " + location.getLatitude());
                                tvLongitude.setText("Longitude: " + location.getLongitude());

                                LatLng lastKnownLoc = new LatLng(location.getLatitude(), location.getLongitude());
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLoc, 15));
                                map.addMarker(new MarkerOptions()
                                        .position(lastKnownLoc)
                                        .title("Here lies your last location")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                            } else {
                                String msg = "No Last Known Location found";
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                            }
                            try {
                                String folder = getFilesDir().getAbsolutePath() + "/Folder";
                                File targetFile = new File(folder, "P10LocationData2.txt");
                                FileWriter writer = new FileWriter(targetFile, true);
                                writer.write(location.getLatitude() + "," + location.getLongitude()  + "\n");
                                writer.flush();
                                writer.close();
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "No Permission.",Toast.LENGTH_LONG).show();
                }
            }
        });

        // mLocationCallback
//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                if (locationResult != null) {
//                    Location data = locationResult.getLastLocation();
//                    double lat = data.getLatitude();
//                    double lng = data.getLongitude();
//                    String msg = "Lat: " + lat + "Lng: " + lng;
//                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    tvLatitude.setText("Latitude: " + lat);
//                    tvLongitude.setText("Longitude: " + lng);
//                    LatLng newLocation = new LatLng(lat, lng);
//                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));
//                    map.addMarker(new MarkerOptions()
//                            .position(newLocation)
//                            .title("Here lies your current location")
//                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
//                    try {
//                        String folder = getFilesDir().getAbsolutePath() + "/Folder";
//                        File targetFile = new File(folder, "P10LocationData2.txt");
//                        FileWriter writer = new FileWriter(targetFile, true);
//                        writer.write(lat + "," + lng + "\n");
//                        writer.flush();
//                        writer.close();
//                    } catch (Exception e) {
//                        Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_SHORT).show();
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };

        btnStartDetector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(checkPermission() == true) {
//                    mLocationRequest = LocationRequest.create();
//                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                    mLocationRequest.setInterval(30);
//                    mLocationRequest.setFastestInterval(100);
//                    mLocationRequest.setSmallestDisplacement(500);
//                    client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
//
//                }

                Intent i = new Intent(MainActivity.this, LocationService.class);
                startService(i);

            }
        });

        btnStopDetector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(checkPermission() == true) {
//                    client.removeLocationUpdates(mLocationCallback);
//                }
                Intent i = new Intent(MainActivity.this, LocationService.class);
                stopService(i);
                Toast.makeText(getApplicationContext(),"Services stopped.",Toast.LENGTH_SHORT).show();
            }

        });

        btnShowRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CheckRecords.class);
                startActivity(i);
            }
        });



        tbMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    startService(new Intent(MainActivity.this, MusicService.class));
                } else {
                    stopService(new Intent(MainActivity.this, MusicService.class));
                }
            }
        });
    }

    private boolean checkPermission() {
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            return false;
        }
    }
}