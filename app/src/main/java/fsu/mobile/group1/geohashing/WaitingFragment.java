package fsu.mobile.group1.geohashing;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class WaitingFragment extends Fragment implements View.OnClickListener {

    private Button mStart;
    private Spinner mode;
    private Spinner point;
    private Spinner radius;
    private EditText GameName;
    private WaitListener waitListener;
    private String Mode;
    private String Point;
    private String Radius;
    private TextInputLayout name;


    public WaitingFragment() {
        // Required empty public constructor
    }

    public interface WaitListener {
    public void startGame(Bundle bundle);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_waiting, container, false);
        GameName=root.findViewById(R.id.game_name);
        mode=root.findViewById(R.id.mode_spinner);
        point=root.findViewById(R.id.point_spinner);
        radius=root.findViewById(R.id.distance_spinner);
        mStart=root.findViewById(R.id.start);
        name=root.findViewById(R.id.input_layout_game_name);
        mStart.setOnClickListener(this);



        //code taken from here--https://android--code.blogspot.com/2015/08/android-spinner-get-selected-item-text.html
        mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Mode=mode.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        point.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Point=point.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        radius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Radius=radius.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return root;
    }

    @Override
    public void onClick(View v) {

        if(GameName.getText().toString().isEmpty()) {

            name.setError("Please Enter a Game Name to Continue");
        }
        else {
            name.setErrorEnabled(false);
            Bundle bundle = new Bundle();
            bundle.putString("gameName", GameName.getText().toString());
            bundle.putString("gameType", Mode);
            bundle.putString("numPoints", Point);
            bundle.putString("Radius", Radius);
            waitListener.startGame(bundle);
        }


    }

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

}
