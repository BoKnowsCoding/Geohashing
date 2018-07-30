package fsu.mobile.group1.geohashing;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


//This is the activity where we will base everything game related
//We will launch the MapsActivity Fragment from here as well as any other fragments needed to support the game
public class GameActivity extends AppCompatActivity implements GameUIFragment.UiListener, ListFragment.ListListener, WaitingFragment.WaitListener {
    private String gameName;
    private String gameType;
    private Toolbar mToolbar;
    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;
    private ArrayList<String> names=new ArrayList<String>();
    private  GameUIFragment myGame;
    private ListFragment myList;
    private WaitingFragment myWait;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private GoogleSignInClient mGoogleSignInClient;
    LoginManager mFBLoginManager;
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        names.add("Test");
        names.add("Test");


        mToolbar = (Toolbar) findViewById(R.id.action_bar);
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
        myGame= new GameUIFragment();
        mManager=getSupportFragmentManager();
        fragTransaction=mManager.beginTransaction();
        fragTransaction.add(R.id.ui_fragment, myGame, "ui");
        fragTransaction.commit();
    }

    //need to add database stuff here as well
    public void onCreateGame(){
        Bundle bundle= new Bundle();
        String userType="Create";
        bundle.putString("userType", userType);
        myWait= new WaitingFragment();
        myWait.setArguments(bundle);
        mManager=getSupportFragmentManager();
        fragTransaction=mManager.beginTransaction();
        fragTransaction.replace(R.id.ui_fragment, myWait, "wait");
        fragTransaction.addToBackStack("to WaitingFragment");
        fragTransaction.commit();

    }

    public void onJoinGame(){
        getGames();

        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list", names);
        myList= new ListFragment();
        myList.setArguments(bundle);
        mManager=getSupportFragmentManager();
        fragTransaction=mManager.beginTransaction();
        fragTransaction.addToBackStack(myGame.toString());
        fragTransaction.replace(R.id.ui_fragment, myList, "list_frag");
        fragTransaction.commit();
//        names.clear();

    }

    //retrieves and lists the current games that are available to join
    public void getGames(){
        Log.i("test", "Called function getGames()");
        db.collection("games")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //create a list of game names
                            Log.i("testing", "Task was successful inside of getGames function");
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Log.d("Games Data", document.getId() + "=>" + document.getData());
                                //add to list on each iteration
                                String gameName= document.getId();
                                names.add(gameName);
                            }

                        }
                        else{
                            Log.w("Error", "Error fetching games", task.getException());
                        }
                    }
                });
    }

    public void onGameSelected(String selection) {
        // TODO: take gameName and gameType from user choice/input if we use this to join
        gameName = "GameTest";
        gameType = "BattleRoyale";
        //Add the user to the selected game document and move them to the lobby
        Bundle bundle= new Bundle();
        String userType="Join";
        String GameName=selection;
        bundle.putString(gameName, GameName);
        bundle.putString("userType", userType);
        myWait= new WaitingFragment();
        myWait.setArguments(bundle);
        mManager=getSupportFragmentManager();
        fragTransaction=mManager.beginTransaction();
        fragTransaction.replace(R.id.ui_fragment, myWait, "wait");
        fragTransaction.commit();
    }

    public void startGame() {
        // TODO: take gameName and gameType from user choice/input (create)
        gameName = "GameTest";
        gameType = "BattleRoyale";

        // check to see if mapsactivity runs
        Intent intent = new Intent(GameActivity.this, MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("gameType", gameType);
        bundle.putString("gameName", gameName);
        intent.putExtras(bundle);
        startActivity(intent);
        /*
        runningGame= new RunningGame();
        fragTransaction=mManager.beginTransaction();
        fragTransaction.addToBackStack(myWait.toString());
        fragTransaction.replace(R.id.wait_fragment, runningGame, "running_frag");
        fragTransaction.commit();
        //so that loaded up the map fragment into the main one here
        //then we need to attach all the listeners which will implement game logic
        */

    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
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

    public void stopGame(){

    }


}


