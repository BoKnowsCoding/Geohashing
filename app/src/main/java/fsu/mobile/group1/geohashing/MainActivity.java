package fsu.mobile.group1.geohashing;

/*Sources
* https://stackoverflow.com/questions/32671004/how-to-change-the-color-of-a-button-in-android-studio/32671191
* Google Android Developer Documentation
* Code from Previous Projects
* Udacity Firebase Course
* */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = MainActivity.class.getCanonicalName();
    //Id codes for permission checks
    static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 3, MY_PERMISSIONS_REQUEST_INTERNET = 4,
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 5, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;

    //Name of our firebase table
    public static final String FIREBASE_TABLE = "users";


    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;
    private FirebaseAuth mAuth;
    private GoogleApiClient mClient;
    private GoogleSignInClient mGoogleSignIn;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();       //initialize firebase variable
        mFirebaseDatabase=FirebaseDatabase.getInstance();   //initialize database reference
        mDatabaseReference=mFirebaseDatabase.getReference(FIREBASE_TABLE); //get references


        /*Right now this does nothing but I think this is what we would use
        to update the map when their last known location changes. What this
        does is gives us a DataSnapchat to work with whenever something in
        our database changes. Not sure if we need this but we'll see*/
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    MyUser user = dataSnapshot.getValue(MyUser.class);
                    Log.d(TAG, "User changed is: " + user.getUserName());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //Just gona check perms programatically
        checkReadPermissions();
        displayLogin();
    }

    //add login fragment to ui
    public void displayLogin(){
        LoginFragment loginFragment=new LoginFragment();
        mManager=getSupportFragmentManager();
        fragTransaction=mManager.beginTransaction();
        fragTransaction.add(R.id.login_fragment, loginFragment, "login_fragment");
        fragTransaction.commit();
    }

    @Override
    public void onConnectionFailed(ConnectionResult CR){
        Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    public void onSignIn(Bundle bundle){
    String user = bundle.getString("user");
    String pass= bundle.getString("pass");
    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
    startActivity(intent);

    //check the Firebase Database to see if the user already exists
    //If they do not, fire a toast and register the user.  Then start the MapsActivity

    }

    //set the google sign in--if the button gets pushed more than once, we crash...gonna have to check for that and do nothing when it's pressed multiple times or something...
    public void onGoogleSignIn(){
        Toast.makeText(getApplicationContext(), "Google Sign In Button Pushed", Toast.LENGTH_SHORT).show();
        GoogleSignInOptions gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)        //create a google sign in options variable
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mClient=new GoogleApiClient.Builder(this).enableAutoManage(MainActivity.this, this) //create a new Google API Client
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        mGoogleSignIn=GoogleSignIn.getClient(this, gso);
        Intent signInIntent=mGoogleSignIn.getSignInIntent();                                                                            //create signIn intent
        startActivityForResult(signInIntent, 1000);                                                                         //start the the activity to the sign in the user
    }

    //after the user has signed in using their email this will call updateUI which will launch the MapsActivity
    @Override
    public void onActivityResult(int RequestCode, int resultCode, Intent data){
        super.onActivityResult(RequestCode, resultCode, data);
        Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
        updateUI(task);

    }

    //starts the MapsActivity
    public void updateUI(Task<GoogleSignInAccount> task){
        try{
            GoogleSignInAccount account=task.getResult(ApiException.class);
            Intent intent=new Intent(MainActivity.this, MapsActivity.class);
            //need to add the task to the UI
            startActivity(intent);
        }
        catch (ApiException e){
                    }

    }


    //Adds a FirebaseUser entry to Firebase database (JSON Datbase) called when registering or logging in
    //^obvs check when logging in if there's someone in the database
    public static void saveToDatabase(FirebaseUser acct) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        //Gonna need to figure out how lastknownloc will be saved and updated, for now empty string
        MyUser user = new MyUser(acct.getDisplayName(), "", 0);

        String key = myRef.child(FIREBASE_TABLE).push().getKey();
        Map<String, Object> postValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + FIREBASE_TABLE + "/" + key, postValues);
        myRef.updateChildren(childUpdates);
    }

    //If there's any more permissions you need just copy and paste one of these and substitute
    public void checkReadPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                    MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

    }
}
