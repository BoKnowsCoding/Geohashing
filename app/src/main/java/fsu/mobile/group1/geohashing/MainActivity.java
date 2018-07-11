package fsu.mobile.group1.geohashing;

/*Sources
* https://stackoverflow.com/questions/32671004/how-to-change-the-color-of-a-button-in-android-studio/32671191
* */

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener {

    private FragmentManager mManager;
    private FragmentTransaction fragTransaction;
//this is a comment
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void onSignIn(Bundle bundle){

    }
    public void onRegister(){

    }
    public void onGoogleSignIn(){

    }
}
