package fsu.mobile.group1.geohashing;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class GameUIFragment extends Fragment implements View.OnClickListener{

    private Button mCreate;
    private Button mJoin;
    private UiListener uiListener;

    public GameUIFragment() {
        // Required empty public constructor
    }

    public interface UiListener{
        public void onCreateGame();
        public void onJoinGame();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root= inflater.inflate(R.layout.fragment_game_ui, container, false);
        mCreate=root.findViewById(R.id.create);
        mJoin=root.findViewById(R.id.join);
        mJoin.setOnClickListener(this);
        mCreate.setOnClickListener(this);



        return root;
    }

    @Override
    public void onClick(View v){

        if(v==mCreate){
            uiListener.onCreateGame();
        }
        else if(v==mJoin){
            uiListener.onJoinGame();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UiListener) {
            uiListener = (UiListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UiListener");
        }
    }

}
