package fsu.mobile.group1.geohashing;

/*Sources
* https://stackoverflow.com/questions/32671004/how-to-change-the-color-of-a-button-in-android-studio/32671191
* Google Android Developer Documentation
* Code from Previous Projects
*Udacity Firebase Course
* */

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, GoogleApiClient.OnConnectionFailedListener{

    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;
    private FirebaseAuth mAuth;
    private GoogleApiClient mClient;
    private GoogleSignInClient mGoogleSignIn;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
//this is a comment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();       //initialize firebase variable
        mFirebaseDatabase=FirebaseDatabase.getInstance();   //initialize database reference
        mMessagesDatabaseReference=mFirebaseDatabase.getReference().child("users"); //get references

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
   Intent intent = new Intent(MainActivity.this, GameActivity.class);
    startActivity(intent);

    //check the Firebase Database to see if the user already exists
    //If they do not, fire a toast and register the user.  Then start the GameActivity

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

    //after the user has signed in using their email this will call updateUI which will launch the GameActivity
    @Override
    public void onActivityResult(int RequestCode, int resultCode, Intent data){
        super.onActivityResult(RequestCode, resultCode, data);
        Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
        updateUI(task);

    }

    //starts the GameActivity
    public void updateUI(Task<GoogleSignInAccount> task){
        try{
            GoogleSignInAccount account=task.getResult(ApiException.class);
            Intent intent=new Intent(MainActivity.this, GameActivity.class);
            //need to add the task to the UI
            startActivity(intent);
        }
        catch (ApiException e){
                    }

    }
}
