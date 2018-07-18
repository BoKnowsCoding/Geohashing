package fsu.mobile.group1.geohashing;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import static fsu.mobile.group1.geohashing.GameActivity.curPlayer;
import static fsu.mobile.group1.geohashing.GameActivity.gameName;

/* TODO:
Implement the join game screen
For game creator, give them ability to start game whenever, which removes
it from the join game list
When a user wins, send out a notification to the peeps, then i guess return to
the GameActivity screen
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
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
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Map<String, Object> userMap;
    //private Map<String, Object> userMap;
    private int localGameScore;
    private static final int DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //THIS IS FOR GETTING STUFF FROM DATABASE
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userMap = new HashMap<>();

        db = FirebaseFirestore.getInstance();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        localGameScore = 0;
    }
    /*
    DocumentReference docRef = db.collection("users").document(currentUser.getUid());
    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                map = document.getData();
            } else {
                Log.d(TAG, "No such document");
            }
        } else {
            Log.d(TAG, "get failed with ", task.getException());
        }
    }
});
     */


    private void createNextNode() {
        Log.i(TAG,"createNExtNode");
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
                            Map<String, String> data = new HashMap<>();
                            double lat = mLastKnownLocation.getLatitude() + randomlat;
                            double lng = mLastKnownLocation.getLongitude() + randomlong;
                            data.put("lat", String.valueOf(lat));
                            data.put("long", String.valueOf(lng));
                            Log.i(TAG, "Lat: " + lat );
                            Log.i(TAG, "lng: " + lng );
                            db.collection(gameName).document("nodeList")
                                    .collection("nodes")
                                    .document("curNode").set(data);

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
                Log.i(TAG, "Location: " + location.getLatitude() + " "
                        + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("You");
                markerOptions.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));

                if(distanceFromGoal(goalLat, goalLong, location.getLatitude(),
                        location.getLongitude()) < 5.0){
                    localGameScore++;
                    if(localGameScore > 4){
                        DocumentReference docRef = db.collection("users")
                                .document(currentUser.getUid());
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    userMap = document.getData();
                                    int overallScore = (Integer) userMap.get("score");
                                    overallScore++;
                                    userMap.put("score",overallScore);
                                    db.collection("users")
                                            .document(currentUser.getUid())
                                            .set(userMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error writing document", e);
                                                }
                                            });
                                } else {
                                    Log.d(TAG, "No such document");
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                            }
                        });
                        Map<String,Object> winMap = new HashMap<>();
                        winMap.put("WIN","Y");
                        db.collection(gameName).document("wins").set(winMap);
                    }
                }

               // db.collection(gameName).document("playerList").collection("players")
                // .document(curPlayer).update("lat", location.getLatitude());
                //db.collection(gameName).document("playerList").collection("players")
                // .document(curPlayer).update("long", location.getLongitude());

            }
        }
    };

    public double distanceFromGoal(double lat1, double long1, double lat2, double long2){
        double earthRadius = 6371000.0;
        double latRad1 = Math.toRadians(lat1), longRad1 = Math.toRadians(long1),
                latRad2 = Math.toRadians(lat2), longRad2 = Math.toRadians(long2);
        double firstPart = (1.0 - Math.cos(latRad2-latRad1))/2.0;
        double secondPart = (Math.cos(latRad1) * Math.cos(latRad2)
                * (1.0 - Math.cos(longRad2-longRad1))/2.0);
        double mainPart = firstPart + secondPart;
        return earthRadius * Math.acos(1.0-(mainPart * 2.0));
    }

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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            // that weirdly long variable is just a result code, declared up top
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            // I believe this sets the location to wherever the user is,
            // and follows them unless they move the map.
            mMap.setMyLocationEnabled(true);
        }
        // Original code in here:
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        createNextNode(); //needs to be removed later
        db.collection(gameName).document("nodeList").collection("nodes")
                .document("curNode").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshots) {
                    Map<String,Object> node = documentSnapshots.getData();
                    Log.i(TAG,node.get("lat").toString() + " "
                            + node.get("long").toString());
                    goalLat = Double.parseDouble(node.get("lat").toString());
                    goalLong = Double.parseDouble(node.get("long").toString());

                    LatLng latLng = new LatLng(goalLat, goalLong);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Goal!");
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    nodeLocationMarker = mMap.addMarker(markerOptions);
        }});


        DocumentReference winDocRef = db.collection(gameName).document("wins");
        winDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
            if (e != null) {
                System.err.println("Listen failed: " + e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Map<String,Object> win = snapshot.getData();
                String yesWin = (String)win.get("WIN");
                if(yesWin.equals("Y")){
                    NotificationManager nm = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);

                    Notification.Builder builder = new Notification.Builder(getApplicationContext());
                    builder.setContentTitle("Game Completed");
                    builder.setContentText("Someone Won!");
                    builder.setAutoCancel(false);
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setWhen(System.currentTimeMillis());
                    nm.notify(42069, build(builder));

                    Intent intent = new Intent(MapsActivity.this, GameActivity.class);
                    startActivity(intent);
                }

            } else {

            }
            }
        });


        DocumentReference docRef = db.collection(gameName).document("nodeList")
                .collection("nodes").document("curNode");
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
                    Log.i(TAG,node.get("lat").toString() + " "
                            + node.get("long").toString());
                    goalLat = Double.parseDouble(node.get("lat").toString());
                    goalLong = Double.parseDouble(node.get("long").toString());
                    if (nodeLocationMarker != null) nodeLocationMarker.remove();
                    LatLng latLng = new LatLng(goalLat, goalLong);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Goal!");
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_ROSE));
                    nodeLocationMarker = mMap.addMarker(markerOptions);

                } else {
                    System.out.print("Current data: null");
                }
            }
        });


    }

    public void deleteFromDocRef(DocumentReference dr, String key){
        Map<String,Object> remove = new HashMap<>();
        remove.put(key,FieldValue.delete());
        dr.update(remove);
    }

    public static Notification build(final Notification.Builder builder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        } else {
            return builder.getNotification();
        }
    }



}
