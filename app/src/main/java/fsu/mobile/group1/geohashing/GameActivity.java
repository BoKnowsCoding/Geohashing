package fsu.mobile.group1.geohashing;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


//This is the activity where we will base everything game related
//We will launch the MapsActivity Fragment from here as well as any other fragments needed to support the game
public class GameActivity extends AppCompatActivity implements GameUIFragment.UiListener {

    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        renderUI();
    }


    public void renderUI(){
        GameUIFragment myGame= new GameUIFragment();
        mManager=getSupportFragmentManager();
        fragTransaction=mManager.beginTransaction();
        fragTransaction.add(R.id.ui_fragment, myGame, "ui");
        fragTransaction.commit();
    }

    public void onCreateGame(){

    }

    public void onJoinGame(){

    }
}
