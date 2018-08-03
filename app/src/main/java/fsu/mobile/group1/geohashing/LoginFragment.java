package fsu.mobile.group1.geohashing;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private String TAG = "LoginFragment";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public interface LoginListener{
    void onSignIn(String email, String password);
    void onGoogleSignIn();
    void onFacebookSignIn();
    }
    private LoginListener loginListener;
    private Button mLogin;
    private Button mRegister;
    private com.google.android.gms.common.SignInButton mGoogle;
    private EditText mUser;
    private EditText mPassword;
    private com.facebook.login.widget.LoginButton mFacebook;
    Map<String, Object> user;


    public LoginFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        //For facebook and google
        mFacebook = root.findViewById(R.id.facebook_sign_in_button);
        mFacebook.setReadPermissions("email", "public_profile");
        //mFacebook.setFragment(this);

        /*set the id's of Buttons and EditTexts*/
        mLogin=root.findViewById(R.id.sign_in);
        mRegister=root.findViewById(R.id.register);
        mGoogle=root.findViewById(R.id.sign_in_button);
        mUser=root.findViewById(R.id.email);
        mPassword=root.findViewById(R.id.password);
        mGoogle.setOnClickListener(this);
        mFacebook.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);

        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginFragment.LoginListener) {
            loginListener = (LoginFragment.LoginListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginListener");
        }
    }

    @Override
    public void onClick(View v){
        Log.i("test","onClick function called");
        Bundle bundle=new Bundle();
        String email;
        String pass;
        //calls appropriate interface method based on which button has been clicked
        if(v==mLogin){
            //package up email and password in bundle and pass to sign-in method
            email = mUser.getText().toString();
            pass = mPassword.getText().toString();

            loginListener.onSignIn(email,pass);
        }
        else if(v==mGoogle) {
            loginListener.onGoogleSignIn();
        }
        else if(v==mRegister) {
            // switch fragments but add this fragment to the back stack
            FragmentManager manager = getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            RegisterFragment fragment = new RegisterFragment();
            trans.replace(R.id.login_fragment, fragment, "login_fragment");
            trans.addToBackStack(this.toString());
            trans.commit();
        }
        else if(v == mFacebook){
            loginListener.onFacebookSignIn();
        }

    }

}
