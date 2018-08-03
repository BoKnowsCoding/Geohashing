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
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
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
import com.google.firebase.firestore.CollectionReference;
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
import java.util.Random;

//import static fsu.mobile.group1.geohashing.GameActivity.curPlayer;

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
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker nodeLocationMarker;
    private double goalLat;
    private double goalLong;
    FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    Map<String, Object> userMap;
    private String gameType;
    private String gameName;
    private String numPoints;
    private int pointsToWin;

    //private Map<String, Object> userMap;
    private int localGameScore;
    private static final int DEFAULT_ZOOM = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // get name and type
        gameName = getIntent().getExtras().getString("gameName");
        Log.i(TAG,"gameName = " + gameName);
        gameType = getIntent().getExtras().getString("gameType");
        numPoints = getIntent().getExtras().getString("numPoints");
        Log.i(TAG,"gameType = " + gameType);

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
        Log.i(TAG,"createNextNode");
        try {
            if (true) {
                Task<Location> locationResult = mFusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            double lat;
                            double lng;
                            Log.i(TAG, "gameType = " + gameType + " gameName = "+ gameName);
                            if (gameType.equals("HashFSU")) {
                                Map<String, String> data = new HashMap<>();
                                Log.i(TAG, "NewLocation FSU Campus");
                                Random r = new Random();

                                // boundaries used for fsu main campus
                                double minLat = 30.435710;
                                double maxLat = 30.444500;
                                double minLong = -84.306026;
                                double maxLong = -84.285728;

                                // generate random latitude and longitude on campus
                                double randomLat = minLat + r.nextFloat() * (maxLat - minLat);
                                Log.i(TAG,"Random Lat: " + randomLat);
                                double randomLong = minLong + r.nextFloat() * (maxLong - minLong);
                                Log.i(TAG, "Random Long: " + randomLong);
                                lat = Double.parseDouble(String.format("%.6f", randomLat));
                                lng = Double.parseDouble(String.format("%.6f", randomLong));
                                mLastKnownLocation = task.getResult();
                                data.put("lat", String.valueOf(lat));
                                data.put("long", String.valueOf(lng));
                                db.collection("games").document(gameName)
                                        .collection("nodeList")
                                        .document("curNode").set(data);
                            } else if (gameType.equals("BattleRoyale")) {
                                Map<String, String> data = new HashMap<>();
                                Log.i(TAG, "NewLocation proximal");
<<<<<<< HEAD
                                double randomlat = Math.random() * Double.parseDouble(maxDistance)/70.0 - Double.parseDouble(maxDistance)/140.0;
                                double randomlong = Math.random() * Double.parseDouble(maxDistance)/70.0 - Double.parseDouble(maxDistance)/140.0;
=======
                                double randomlat = Math.random() * .002 - .001;
                                double randomlong = Math.random() * .002 - .001;
>>>>>>> 89afd54880205128dc70938d66e2d75094829de9
                                randomlat = Double.parseDouble(String.format("%.6f", randomlat));
                                randomlong = Double.parseDouble(String.format("%.6f", randomlong));
                                mLastKnownLocation = task.getResult();
                                lat = Double.parseDouble(String.format("%.6f", mLastKnownLocation.getLatitude())) + randomlat;
                                lng = Double.parseDouble(String.format("%.6f", mLastKnownLocation.getLongitude())) + randomlong;
                                data.put("lat", String.valueOf(lat));
                                data.put("long", String.valueOf(lng));
                                db.collection("games").document(gameName)
                                        .collection("nodeList")
                                        .document("curNode").set(data);
                            } else if(gameType.equals("FreeForAll")){
                                Log.i(TAG,"FFA game going");
                                Map<String, Double[]> data = new HashMap<>();
                                int intNumPoints = Integer.getInteger(numPoints);
                                pointsToWin = (int)Math.ceil(intNumPoints/4.0);
                                Double[] latArray = new Double[intNumPoints];
                                Double[] longArray = new Double[intNumPoints];
                                for(int i = 0; i < intNumPoints; i++){
                                    double randomLat = Math.random() * .002 - .001;
                                    double randomLong = Math.random() * .002 - .001;
                                    randomLat = Double.parseDouble(String.format("%.6f", randomLat));
                                    randomLong = Double.parseDouble(String.format("%.6f", randomLong));
                                    mLastKnownLocation = task.getResult();
                                    latArray[i] = Double.parseDouble(String.format("%.6f", mLastKnownLocation.getLatitude())) + randomLat;
                                    longArray[i] = Double.parseDouble(String.format("%.6f", mLastKnownLocation.getLongitude())) + randomLong;
                                    }
                                data.put("lat", latArray);
                                data.put("long", longArray);
                                db.collection("games").document(gameName)
                                        .collection("nodeList")
                                        .document("curNode").set(data);
                            }
                            else {
                                Map<String, String> data = new HashMap<>();
                                // if you properly set the game type this should never happen,
                                // but these need to be defined to compile
                                Log.i(TAG,"Error: No gameType set");
                                lat = 0;
                                lng = 0;
                                data.put("lat", String.valueOf(lat));
                                data.put("long", String.valueOf(lng));
                                db.collection("games").document(gameName)
                                        .collection("nodeList")
                                        .document("Nodes").set(data);
                            }

                            //Log.i(TAG, "Lat: " + lat );
                            //Log.i(TAG, "lng: " + lng );
                            //
                            //db.collection(gameName).document("nodeList")
                            //        .collection("nodes")
                            //        .document("curNode").set(data);
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
            Log.i(TAG,"LocationUpdate");
            List<Location> locationList = locationResult.getLocations();
            Log.i(TAG,"Location call back hit: " + locationList.size());
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
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));


                if(gameType == "FreeForAll"){
                    final Location newLoc = location;
                    DocumentReference docRef = db.collection("games").document(gameName)
                            .collection("nodeList").document("Nodes");
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                if(document.exists()){
                                    Map<String,Object> latLongMap = document.getData();
                                    Double[] latArray = (Double[])latLongMap.get("lat");
                                    Double[] longArray = (Double[])latLongMap.get("long");
                                    for(int i = 0; i < Integer.getInteger(numPoints); i++){
                                        if(distanceFromGoal(latArray[i], longArray[i], Double.parseDouble(String.format("%.6f", newLoc.getLatitude())), Double.parseDouble(String.format("%.6f", newLoc.getLongitude()))) < 5.0){
                                            localGameScore++;
                                            if(localGameScore > pointsToWin){
                                               DocumentReference docRef2 = db.collection("users").document(currentUser.getUid());
                                                docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                        if(task2.isSuccessful()){
                                                            DocumentSnapshot document2 = task2.getResult();
                                                            if(document2.exists()){
                                                                userMap = document2.getData();
                                                                int overallScore = (Integer) userMap.get("score");
                                                                overallScore++;
                                                                userMap.put("score",overallScore);
                                                                db.collection("users").document(currentUser.getUid()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG,"DocumentSnapshot successfully written");
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                });
                                                Map<String, Object> winMap = new HashMap<>();
                                                winMap.put("WIN", "Y");
                                                db.collection("games").document(gameName).collection("wins").document("isWin").set(winMap);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                    //Double[] latList = db.collection("games").document(gameName).collection("nodeList").document("Nodes").get();
                }else {
                    if (distanceFromGoal(goalLat, goalLong, Double.parseDouble(String.format("%.6f", location.getLatitude())),
                            Double.parseDouble(String.format("%.6f", location.getLongitude()))) < 5.0) {
                        localGameScore++;
                        Log.i(TAG, "Local score" + localGameScore);
                        //Toast.makeText(MapsActivity.this,
                        //        "You've captured: " + localGameScore + " nodes!", Toast.LENGTH_SHORT).show();
                        if (localGameScore > 4) {
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
                                            userMap.put("score", overallScore);
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
                            Map<String, Object> winMap = new HashMap<>();
                            winMap.put("WIN", "Y");
                            db.collection("games").document(gameName).collection("wins").document("isWin").set(winMap);
                        } else {
                            createNextNode();
                        }
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
        Log.i(TAG, "Goal" + lat1 + " " + long1);
        Log.i(TAG,"Distance from goal: " + earthRadius * Math.acos(1.0-(mainPart * 2.0)));
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



        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mMap.setMyLocationEnabled(true);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        // Original code in here:
        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        /*
        createNextNode(); //needs to be removed later
        db.collection(gameName).document("nodeList").collection("nodes")
                .document("curNode").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshots) {
                    Map<String,Object> node = documentSnapshots.getData();
                    Log.i(TAG,"First node " +node.get("lat").toString() + " "
                            + node.get("long").toString());
                    goalLat = Double.parseDouble(node.get("lat").toString());
                    goalLong = Double.parseDouble(node.get("long").toString());
                    if(nodeLocationMarker != null) nodeLocationMarker.remove();
                    LatLng latLng = new LatLng(goalLat, goalLong);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title("Goal!");
                    markerOptions.icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    nodeLocationMarker = mMap.addMarker(markerOptions);
        }});
*/
<<<<<<< HEAD
        Map<String, String> functionData = new HashMap<>();
        functionData.put("theGameName", gameName);
        db.collection("games").document(gameName).set(functionData);
        DocumentReference winDocRef = db.collection("games").document(gameName).collection("wins").document("isWin");
=======

        DocumentReference winDocRef = db.collection(gameName).document("wins");
>>>>>>> 89afd54880205128dc70938d66e2d75094829de9
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
                        Toast.makeText(MapsActivity.this,
<<<<<<< HEAD
                                "Someone has won!", Toast.LENGTH_SHORT).show();
                        db.collection("games").document(gameName)
                                .collection("nodeList")
=======
                                "You've won!", Toast.LENGTH_SHORT).show();
                        db.collection(gameName).document("nodeList").collection("nodes")
>>>>>>> 89afd54880205128dc70938d66e2d75094829de9
                                .document("curNode").delete();
                        db.collection("games").document(gameName).collection("wins").document("isWin").delete();
                        db.collection("games").document(gameName).delete();

                        /*
                        NotificationManager nm = (NotificationManager)getSystemService(MapsActivity.this.NOTIFICATION_SERVICE);

                        Notification.Builder builder = new Notification.Builder(MapsActivity.this);
                        builder.setContentTitle("Game Completed");
                        builder.setContentText("Someone Won!");
                        builder.setAutoCancel(false);
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        builder.setWhen(System.currentTimeMillis());
                        nm.notify(42069, build(builder));
*/
                        Intent intent = new Intent(MapsActivity.this, GameActivity.class);
                        startActivity(intent);
                    }

                } else {

                }
            }
        });

        createNextNode();
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
                    Log.i(TAG,"Table update " + node.get("lat").toString() + " "
                            + node.get("long").toString());
                    goalLat = Double.parseDouble(node.get("lat").toString());
                    goalLong = Double.parseDouble(node.get("long").toString());
                    if(nodeLocationMarker != null) nodeLocationMarker.remove();
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
