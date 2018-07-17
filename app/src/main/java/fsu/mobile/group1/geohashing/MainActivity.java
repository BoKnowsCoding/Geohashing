package fsu.mobile.group1.geohashing;

/*Sources
* https://stackoverflow.com/questions/32671004/how-to-change-the-color-of-a-button-in-android-studio/32671191
* Google Android Developer Documentation
* Code from Previous Projects
* Udacity Firebase Course
* */

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener,
        GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = MainActivity.class.getCanonicalName();
    //Id codes for permission checks
    static final int MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE = 3,
            MY_PERMISSIONS_REQUEST_INTERNET = 4,
            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 5,
            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;

    //Name of our firebase table
    public static final String FIREBASE_TABLE = "users";


    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;
    private FirebaseAuth mAuth;                     //don't delete
    private GoogleApiClient mClient;
    private GoogleSignInClient mGoogleSignIn;
    private CallbackManager callbackManager;
    private FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();       //don't delete

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignIn = GoogleSignIn.getClient(this, gso);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null && !accessToken.isExpired()){
                LoginManager.getInstance().logInWithReadPermissions(this,
                        Arrays.asList("public_profile"));
        }
        checkReadPermissions();
        displayLogin();
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
            updateUI(currentUser);
        // forwards the user directly to GameActivity if they signed in in a previous session
        GoogleSignInAccount googleUser = GoogleSignIn.getLastSignedInAccount(this);
        if (googleUser != null) {
            Intent intent = new Intent(this, GameActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
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

    public void onSignIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            final DocumentReference userRef = db.collection("users").document(user.getUid());
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        FirebaseUser nUser = mAuth.getCurrentUser();
                                        if (document != null) {
                                            //The user exists don't need to add him
                                            updateUI(nUser);
                                        }


                                        else {
                                            //This stores a non existent user into our database
                                            //The user doesn't exist so we add him
                                            Map<String, Object> userObj = new HashMap<>();
                                            userObj.put("displayName", nUser.getDisplayName());
                                            userObj.put("email", nUser.getEmail());
                                            userObj.put("score", 0);
                                            db.collection("users").document(nUser.getUid())
                                                    .set(userObj)
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
                                            updateUI(nUser);
                                        }

                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
    }

    //set the google sign in--if the button gets pushed more than once, we crash...
    // gonna have to check for that and do nothing when it's pressed multiple times or something...
    public void onGoogleSignIn(){
        Toast.makeText(getApplicationContext(),
                "Google Sign In Button Pushed", Toast.LENGTH_SHORT).show();

        //create signIn intent
        Intent signInIntent=mGoogleSignIn.getSignInIntent();
        //start the the activity to the sign in the user
        startActivityForResult(signInIntent, 1000);
    }

    public void onFacebookSignIn(){
        callbackManager = CallbackManager.Factory.create();
        Log.i("MainActivity", "in onFacebookSignIn()");
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("MainActivity", "in onSuccess");
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Facebook login cancelled",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Facebook login error",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            final DocumentReference userRef = db.collection("users")
                                    .document(user.getUid());
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        FirebaseUser nUser = mAuth.getCurrentUser();

                                        if (document != null) {
                                            //The user exists don't need to add him
                                            updateUI(nUser);
                                        }


                                        else {
                                            //This stores a non existent user into our database
                                            //The user doesn't exist so we add him
                                            Map<String, Object> userObj = new HashMap<>();
                                            userObj.put("displayName", nUser.getDisplayName());
                                            userObj.put("email", nUser.getEmail());
                                            userObj.put("score", 0);
                                            db.collection("users").document(nUser.getUid())
                                                    .set(userObj)
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
                                            updateUI(nUser);
                                        }

                                    }
                                }
                        });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    //after the user has signed in using their email this will call updateUI
    // which will launch the MapsActivity
    @Override
    public void onActivityResult(int RequestCode, int resultCode, Intent data){
        super.onActivityResult(RequestCode, resultCode, data);
        if(RequestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        } else{
            //callbackManager.onActivityResult(RequestCode, resultCode, data);
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            final DocumentReference userRef = db.collection("users")
                                    .document(user.getUid());
                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        FirebaseUser nUser = mAuth.getCurrentUser();

                                        if (document != null) {
                                            //The user exists don't need to add him
                                            updateUI(nUser);
                                        }


                                        else {
                                            //The user doesn't exist so we add him
                                            Map<String, Object> userObj = new HashMap<>();
                                            userObj.put("displayName", nUser.getDisplayName());
                                            userObj.put("email", nUser.getEmail());
                                            userObj.put("score", 0);
                                            db.collection("users").document(nUser.getUid())
                                                    .set(userObj)
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
                                            updateUI(nUser);
                                        }

                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    public void updateUI(FirebaseUser mUser)
    {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (mUser != null)
            intent.putExtra("userId", mUser.getUid());
        startActivity(intent);
    }


    //If there's any more permissions you need just copy and paste one of these and substitute
    public void checkReadPermissions()
    {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE},
                    MY_PERMISSIONS_REQUEST_ACCESS_NETWORK_STATE);
        if (ContextCompat.checkSelfPermission(this
                , android.Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

    }
}
