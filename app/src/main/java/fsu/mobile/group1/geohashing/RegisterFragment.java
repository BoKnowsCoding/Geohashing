package fsu.mobile.group1.geohashing;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;




/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private Button mRegister;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
   // private FirebaseAuth mAuth;

    private String TAG = "RegisterFragment";

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       // mAuth = FirebaseAuth.getInstance();

        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_register, container, false);

        /*set the id's of Buttons and EditTexts*/
        mRegister=root.findViewById(R.id.register_finish);
        mEmail=root.findViewById(R.id.username_reg);
        mPassword=root.findViewById(R.id.register_password);
        mConfirmPassword=root.findViewById(R.id.confirm_password);
        mRegister.setOnClickListener(this);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof RegisterFragment.LoginListener) {
            loginListener = (RegisterFragment.LoginListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginListener");
        }
        */
    }

    @Override
    public void onClick(View v){
        Log.i("test","onClick function called");
        Bundle bundle=new Bundle();
        String user=null;
        String pass=null;
        //calls appropriate interface method based on which button has been clicked
        if(v==mRegister){
            checkForm();
        }

    }

    private boolean checkForm() {
        if (mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {
            //registerAccount(mEmail.getText().toString(),mPassword.getText().toString());
            return true;
        }
        return false;
    }

    /*
    private void registerAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //DatabaseReference myRef = database.getReference();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //User is now in database no need to check if already exists because
                            //This this is for registering
                            //MainActivity.saveToDatabase(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
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
