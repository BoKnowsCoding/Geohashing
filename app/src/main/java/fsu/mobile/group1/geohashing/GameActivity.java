package fsu.mobile.group1.geohashing;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


//This is the activity where we will base everything game related
//We will launch the MapsActivity Fragment from here as well as any other fragments needed to support the game
public class GameActivity extends AppCompatActivity implements GameUIFragment.UiListener, ListFragment.ListListener, WaitingFragment.WaitListener {

    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;
    private ArrayList<String> names=new ArrayList<String>();
    private  GameUIFragment myGame;
    private ListFragment myList;
    private WaitingFragment myWait;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        renderUI();
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
        fragTransaction.commit();

    }

    public void onJoinGame(){
        getGames();

        Bundle bundle = new Bundle();
         names.add("Test");
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

    public void onGameSelected(String selection){
        //Add the user to the selected game document and move them to the lobby
        Bundle bundle= new Bundle();
        String userType="Join";
        String GameName=selection;
        bundle.putString("GameName", GameName);
        bundle.putString("userType", userType);
        myWait= new WaitingFragment();
        myWait.setArguments(bundle);
        mManager=getSupportFragmentManager();
        fragTransaction=mManager.beginTransaction();
        fragTransaction.replace(R.id.ui_fragment, myWait, "wait");
        fragTransaction.commit();

    }

    public void LaunchGame(){
    }


}


