package fsu.mobile.group1.geohashing;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    public interface LoginListener{
    public void onSignIn(Bundle bundle);
    public void onRegister();
    public void onGoogleSignIn();
    }
    private LoginListener loginListener;
    private Button mLogin;
    private Button mRegister;
    private com.google.android.gms.common.SignInButton mGoogle;
    private EditText mUser;
    private EditText mPassword;

    public LoginFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_login, container, false);

        /*set the id's of Buttons and EditTexts*/
        mLogin=root.findViewById(R.id.sign_in);
        mRegister=root.findViewById(R.id.register);
        mGoogle=root.findViewById(R.id.sign_in_button);
        mUser=root.findViewById(R.id.username);
        mPassword=root.findViewById(R.id.password);
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
        Bundle bundle=new Bundle();
        String user=null;
        String pass=null;
        //calls appropriate interface method based on which button has been clicked
        if(v==mLogin){
        //package up user and password in bundle and pass to sign-in method
        user=mUser.getText().toString();
        pass=mPassword.getText().toString();
        bundle.putString("user", user);
        bundle.putString("pass", pass);
        loginListener.onSignIn(bundle);
        }
        else if(v==mRegister){
            loginListener.onRegister();
        }
        else if(v==mGoogle){
            loginListener.onGoogleSignIn();
        }

    }

}
