package fsu.mobile.group1.geohashing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;


//This is the activity where we will base everything game related
//We will launch the MapsActivity Fragment from here as well as any other fragments needed to support the game
public class GameActivity extends AppCompatActivity implements GameUIFragment.UiListener {
    public static String gameName;
    private Toolbar mToolbar;
    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;
    private GoogleSignInClient mGoogleSignInClient;
    LoginManager mFBLoginManager;
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        gameName = "GameTest"; //tb removed later
//        getSupportActionBar().hide();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        mFBLoginManager = LoginManager.getInstance();
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //onCreateGame(); //for testing purposes
        renderUI();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.sign_out:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void renderUI(){
        GameUIFragment myGame= new GameUIFragment();
        mManager=getSupportFragmentManager();
        fragTransaction=mManager.beginTransaction();
        fragTransaction.add(R.id.ui_fragment, myGame, "ui");
        fragTransaction.commit();
    }

    public void onCreateGame(){
        Log.i("GameActivity", "Oncreategame");
            RunningGame newGame = new RunningGame();
            mManager = getSupportFragmentManager();
            fragTransaction = mManager.beginTransaction();
            fragTransaction.add(R.id.ui_fragment,newGame);
            fragTransaction.commit();
    }

    public void onJoinGame(){

    }

    //retrieves and lists the current games that are available to join
    public void getGames(){

    }

    public void startGame()
    {
       FragmentManager fm = getSupportFragmentManager();
        RunningGame fragment = new RunningGame();
        fm.beginTransaction().add(R.id.ui_fragment,fragment).commit();
        //so that loaded up the map fragment into the main one here
        //then we need to attach all the listeners which will implement game logic
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(GameActivity.this,
                                MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        mFBLoginManager.logOut();
    }


}


