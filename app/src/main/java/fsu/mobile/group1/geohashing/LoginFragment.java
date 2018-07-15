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

import com.google.firebase.firestore.FirebaseFirestore;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private String TAG = "LoginFragment";

    public interface LoginListener{
    void onSignIn(Bundle bundle);
    void onGoogleSignIn();
    }
    private LoginListener loginListener;
    private Button mLogin;
    private Button mRegister;
    private com.google.android.gms.common.SignInButton mGoogle;
    private EditText mUser;
    private EditText mPassword;

    //private FirebaseAuth mAuth;


    public LoginFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        /*set the id's of Buttons and EditTexts*/
        mLogin=root.findViewById(R.id.sign_in);
        mRegister=root.findViewById(R.id.register);
        mGoogle=root.findViewById(R.id.sign_in_button);
        mUser=root.findViewById(R.id.username);
        mPassword=root.findViewById(R.id.password);
        mGoogle.setOnClickListener(this);
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
        String user;
        String pass;
        //calls appropriate interface method based on which button has been clicked
        if(v==mLogin){
            //package up user and password in bundle and pass to sign-in method
            user=mUser.getText().toString();
            pass=mPassword.getText().toString();

            bundle.putString("user", user);
            bundle.putString("pass", pass);
            loginListener.onSignIn(bundle);

            //onSignIn(user,pass);
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

    }

    /*
    private void onSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //CHECKING OUR DATABASE FOR USER, ADD THEM IF DOESN'T EXIST YET
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users");
                            //Users are stored with uIds automatically
                            myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    //This user literally exists cuz since this is an inner class id have to declare user
                                    //final to save to database and screw that
                                    FirebaseUser userForNestedClass = mAuth.getCurrentUser();
                                    //If user doesn't exist we'll add it to our database otherwise we'll continue on
                                    if(!dataSnapshot.exists())
                                    {
                                        MainActivity.saveToDatabase(userForNestedClass);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        // do stuff ?
    }
*/

}
