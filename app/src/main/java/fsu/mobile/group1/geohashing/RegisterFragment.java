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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    private Button mRegister;
    private EditText mDisplay;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String TAG = "RegisterFragment";

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        /*set the id's of Buttons and EditTexts*/
        mRegister=root.findViewById(R.id.register_finish);
        mDisplay=root.findViewById(R.id.displayName_reg);
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

        //calls appropriate interface method based on which button has been clicked
        if(v==mRegister){
            if (checkForm())
            {
                mAuth.createUserWithEmailAndPassword(mEmail.getText().toString(), mPassword.getText().toString())
                        .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    final DocumentReference userRef = db.collection("users").document(user.getUid());
                                    userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                FirebaseUser nUser = mAuth.getCurrentUser();
                                                if (document != null) {
                                                    //The user exists don't need to add him
                                                    updateUI(nUser);
                                                }


                                                else {
                                                    //This stores a non existent user into our database
                                                    //The user doesn't exist so we add him
                                                    Map<String, Object> userObj = new HashMap<>();
                                                    userObj.put("displayName", mDisplay.getText().toString());
                                                    userObj.put("email", nUser.getEmail());
                                                    userObj.put("score", 0);
                                                    db.collection("users").document(nUser.getUid())
                                                            .set(userObj)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Log.w(TAG, "Error writing document", e);
                                                                }
                                                            });
                                                    updateUI(nUser);
                                                }

                                            }
                                        }
                                    });
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                    Toast.makeText(getActivity().getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                // ...
                            }
                        });
            }
            else
            {
                Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean checkForm() {
        if (mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {
            //registerAccount(mEmail.getText().toString(),mPassword.getText().toString());
            return true;
        }
        return false;
    }

    public void updateUI(FirebaseUser mUser)
    {
        Intent intent = new Intent(getActivity(), GameActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (mUser != null)
            intent.putExtra("userId", mUser.getUid());
        startActivity(intent);
    }
}
