package fsu.mobile.group1.geohashing;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.transition.Slide;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.util.Log;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//This is the activity where we will base everything game related
//We will launch the MapsActivity Fragment from here as well as any other fragments needed to support the game
public class GameActivity extends AppCompatActivity implements GameUIFragment.UiListener, ListFragment.ListListener, WaitingFragment.WaitListener, themeFragment.ThemeListener {
    private String gameName;
    private String gameType;
    private Toolbar mToolbar;
    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;
    private ArrayList<String> names = new ArrayList<String>();
    private GameUIFragment myGame;
    private ListFragment myList;
    private WaitingFragment myWait;
    private ViewGroup mRoot;
    private themeFragment theme;

    // Access a Cloud Firestore instance from your Activity
    private FirebaseFirestore db;

    private GoogleSignInClient mGoogleSignInClient;
    LoginManager mFBLoginManager;
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //mRoot=findViewById(R.id.root);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        //getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        //setContentView(R.layout.activity_game);


        mToolbar = (Toolbar) findViewById(R.id.action_bar);
        gameName = "Brandon is so cool wow"; //tb removed later
//        getSupportActionBar().hide();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mFBLoginManager = LoginManager.getInstance();
        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build();
        db.setFirestoreSettings(settings);
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
            case R.id.theme:
                loadThemeSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void renderUI() {
        Slide mySlide = new Slide(Gravity.RIGHT);
        myGame = new GameUIFragment();
        mySlide.setStartDelay(10000);
        myGame.setEnterTransition(mySlide);
        mManager = getSupportFragmentManager();
        fragTransaction = mManager.beginTransaction();
        fragTransaction.add(R.id.ui_fragment, myGame, "ui");
        fragTransaction.commit();
    }

    //need to add database stuff here as well
    public void onCreateGame() {

        Slide exitFade= new Slide();        //used to load fade out the current fragment
        Slide enterFade = new Slide(Gravity.LEFT);
       // exitFade.setDuration(200);
        enterFade.setStartDelay(500);
        //enterFade.setDuration(200);
        Bundle bundle = new Bundle();
        //  String userType="Create";
        //bundle.putString("userType", userType);

        myWait = new WaitingFragment();
        myWait.setEnterTransition(enterFade);
        myGame.setExitTransition(exitFade);
        //myWait.setArguments(bundle);
        mManager = getSupportFragmentManager();
        fragTransaction = mManager.beginTransaction();
        fragTransaction.replace(R.id.ui_fragment, myWait, "wait");
        fragTransaction.addToBackStack("to WaitingFragment");
        fragTransaction.commit();

    }

    public void onJoinGame() {
        getGames();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list", names);
        myList = new ListFragment();
        myList.setArguments(bundle);
        mManager = getSupportFragmentManager();
        fragTransaction = mManager.beginTransaction();
        fragTransaction.addToBackStack(myGame.toString());
        fragTransaction.replace(R.id.ui_fragment, myList, "list_frag");
        fragTransaction.commit();
//        names.clear();
    }

    //retrieves and lists the current games that are available to join
    public void getGames() {
        Log.i("test", "Called function getGames()");
        Map<String, Object> asdf = new HashMap<>();
        asdf.put("blah", "blasdfasdf");
        db.collection("games").document("aasdfsdf").set(asdf);
        Log.i("ASDF ACTIVITY", db.collection("games").document("aasdfsdf").getId());
        db.collection("games")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //create a list of game names
                            Log.i("testing", "Task was successful inside of getGames function");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Games Data", document.getId() + "=>" + document.getData());
                                //add to list on each iteration
                                String gameName = document.getId();
                                names.add(gameName);
                            }
                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("list", names);
                            myList = new ListFragment();
                            myList.setArguments(bundle);
                            mManager = getSupportFragmentManager();
                            fragTransaction = mManager.beginTransaction();
                            fragTransaction.addToBackStack(myGame.toString());
                            fragTransaction.replace(R.id.ui_fragment, myList, "list_frag");
                            fragTransaction.commit();
                        }
                    }
                });

    }

    public void onGameSelected(String selection) {
        // TODO: take gameName and gameType from user choice/input if we use this to join
        gameName = "GameTest";
        gameType = "BattleRoyale";
        //Add the user to the selected game document and move them to the lobby
        Bundle bundle = new Bundle();
        String userType = "Join";
        String GameName = selection;
        bundle.putString(gameName, GameName);
        bundle.putString("userType", userType);
        myWait = new WaitingFragment();
        myWait.setArguments(bundle);
        mManager = getSupportFragmentManager();
        fragTransaction = mManager.beginTransaction();
        fragTransaction.replace(R.id.ui_fragment, myWait, "wait");
        fragTransaction.commit();
    }

    public void startGame(Bundle data) {
        // TODO: take gameName and gameType from user choice/input (create)
        gameName = "GameTest";
        gameType = "BattleRoyale";
        //Explode explode = new Explode();
      //  myWait.setExitTransition(explode);
        // check to see if mapsactivity runs
        // Map entry for game type (1, 2, 3)
        // Number of points to win (currently set at 5)
        // How far away each node should be (currently set at 2 km)
        // nodes table
        // Winner entry
        db.collection("games").document("I wanna die lmao");
        Map<String, Object> typeData = new HashMap<>();
        typeData.put("GameType", 1);
        db.collection("games").document("I wanna die lmao").collection("GameType").document("GameType").set(typeData);
        Map<String, Object> numPoints = new HashMap<>();
        numPoints.put("numPoints", 5);
        db.collection("games").document("I wanna die lmao").collection("numPoints").document("num").set(numPoints);
        Map<String, Object> setDistance = new HashMap<>();
        setDistance.put("Distance", 1.0);
        db.collection("games").document("I wanna die lmao").collection("distance").document("distance").set(setDistance);

        Intent intent = new Intent(GameActivity.this, MapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("gameType", gameType);
        bundle.putString("gameName", gameName);
        intent.putExtras(bundle);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
        public void signOut() {
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

        public void loadThemeSettings(){
            theme=new themeFragment();
            mManager = getSupportFragmentManager();
            fragTransaction = mManager.beginTransaction();
            fragTransaction.replace(R.id.ui_fragment, theme, "wait");
            fragTransaction.commit();
        }

        public void reloadTheme(){
            theme=new themeFragment();
            mManager = getSupportFragmentManager();
            fragTransaction = mManager.beginTransaction();
            fragTransaction.replace(R.id.theme_fragment, theme, "theme");
            fragTransaction.commit();
        }
}


