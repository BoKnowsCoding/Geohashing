package fsu.mobile.group1.geohashing;

/*Sources
* https://stackoverflow.com/questions/32671004/how-to-change-the-color-of-a-button-in-android-studio/32671191
* Google Android Developer Documentation
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


public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, GoogleApiClient.OnConnectionFailedListener{

    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;
    private FirebaseAuth mAuth;
    private GoogleApiClient mClient;
    private GoogleSignInClient mGoogleSignIn;
//this is a comment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();       //initialize firebase variable

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
        Toast.makeText(getApplicationContext(), "Connection Failed1", Toast.LENGTH_SHORT).show();
    }

    public void onSignIn(Bundle bundle){
    String user = bundle.getString("user");
    String pass= bundle.getString("pass");

    }
    public void onRegister(){

    }
    //set the google sign in
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

    //after the user has signed in using their email
    @Override
    public void onActivityResult(int RequestCode, int resultCode, Intent data){
        super.onActivityResult(RequestCode, resultCode, data);
        Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
        updateUI(task);

    }

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
