package fsu.mobile.group1.geohashing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static fsu.mobile.group1.geohashing.GameActivity.curPlayer;
import static fsu.mobile.group1.geohashing.GameActivity.gameName;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private FirebaseFirestore db;
    Location mLastKnownLocation;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker nodeLocationMarker;
    private double goalLat;
    private double goalLong;
    FusedLocationProviderClient mFusedLocationClient;
    private static final int DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MapsActivity","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        db = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private void createNextNode() {
        Log.i("MapsActivity","createNExtNode");
        try {
            if (true) {
                Task<Location> locationResult = mFusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            double randomlat = Math.random() *.002 -.001;
                            double randomlong = Math.random() *.002 -.001;
                            mLastKnownLocation = task.getResult();
                            Map<String, Object> data = new HashMap<>();
                            double lat = mLastKnownLocation.getLatitude() + randomlat;
                            double lng = mLastKnownLocation.getLongitude() + randomlong;
                            data.put("lat", lat);
                            data.put("long", lng);
                            Log.i("MapsActivity", "Lat: " + lat );
                            Log.i("MapsActivity", "lng: " + lng );
                            db.collection(gameName).document("nodeList").collection("nodes").document("curNode")
                                    .set(data);

                        }

                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }
    //this code keeps the cameria centered on you
    //https://stackoverflow.com/questions/44992014/how-to-get-current-location-in-googlemap-using-fusedlocationproviderclient
    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest


                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("You");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
               // db.collection(gameName).document("playerList").collection("players").document(curPlayer).update("lat", location.getLatitude());
                //db.collection(gameName).document("playerList").collection("players").document(curPlayer).update("long", location.getLongitude());

            }
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            // that weirdly long variable is just a result code, declared up top
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            // I believe this sets the location to wherever the user is, and follows them unless they move the map.
            mMap.setMyLocationEnabled(true);
        }
        // Original code in here:
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        createNextNode(); //needs to be removed later
        db.collection(gameName).document("nodeList").collection("nodes").document("curNode").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshots) {
                Map<String,Object> node = documentSnapshots.getData();
                Log.i("MapsActivyt",node.get("lat").toString() + " " + node.get("long").toString());
                goalLat = Double.parseDouble(node.get("lat").toString());
                goalLong = Double.parseDouble(node.get("long").toString());

                LatLng latLng = new LatLng(goalLat, goalLong);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Goal!");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                nodeLocationMarker = mMap.addMarker(markerOptions);

        }});




        DocumentReference docRef = db.collection(gameName).document("nodeList").collection("nodes").document("curNode");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed: " + e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    Map<String,Object> node = snapshot.getData();
                    Log.i("MapsActivity",node.get("lat").toString() + " " + node.get("long").toString());
                    goalLat = Double.parseDouble(node.get("lat").toString());
                    goalLong = Double.parseDouble(node.get("long").toString());
                    nodeLocationMarker.remove();
                    LatLng latLng = new LatLng(goalLat, goalLong);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Goal!");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    nodeLocationMarker = mMap.addMarker(markerOptions);

                } else {
                    System.out.print("Current data: null");
                }
            }
        });


    }






}
