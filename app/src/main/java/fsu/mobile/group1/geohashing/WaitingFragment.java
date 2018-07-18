package fsu.mobile.group1.geohashing;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class WaitingFragment extends Fragment implements View.OnClickListener {

    private Button mStart;
    private TextView waitText;
    private WaitListener waitListener;

    public WaitingFragment() {
        // Required empty public constructor
    }

    public interface WaitListener {
        public void LaunchGame();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_waiting, container, false);
        mStart = root.findViewById(R.id.start);
        waitText = root.findViewById(R.id.wait_start);
        mStart.setOnClickListener(this);
        mStart.setEnabled(false);
        mStart.setVisibility(View.INVISIBLE);
        waitText.setVisibility(View.INVISIBLE);
        String userType;
        String GameName;
        Bundle bundle= getArguments();
        if(bundle!=null) {
             userType = bundle.getString("userType");
             GameName= bundle.getString("GameName");

            if(userType.equals("Join")){
                waitText.setVisibility(View.VISIBLE);
            }
            else if(userType.equals("Create")){
                mStart.setVisibility(View.VISIBLE);
                mStart.setEnabled(true);
            }

            WaitForGame(GameName);
        }
        else{
            Toast.makeText(getContext(),"Error with Game sorry...", Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    @Override
    public void onClick(View v) {
        //will change the state of the game to "START" when the startGame button is clicked
    }
/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WaitListener) {
            waitListener = (WaitListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WaitListener");
        }

    }
*/
    private void WaitForGame(String name){

    }
}
