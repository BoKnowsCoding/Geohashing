package fsu.mobile.group1.geohashing;


import android.app.LauncherActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ListFragment extends Fragment {

    private ListView listView;
    private ListListener listListener;
    public ListFragment() {
        // Required empty public constructor
    }

    public interface ListListener{
        public void onGameSelected(String selection);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_list, container, false);
        listView=root.findViewById(R.id.game_list);
        Bundle bundle=getArguments();
        if(bundle!=null){
            ArrayList<String> myArray=bundle.getStringArrayList("list");
            ArrayAdapter<String> items= new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, myArray );
            listView.setAdapter(items);
            Toast.makeText(getContext(), "Fetching Games List...", Toast.LENGTH_SHORT).show();

        }
        else
            Toast.makeText(getContext(), "Sorry no games found!", Toast.LENGTH_SHORT).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Log.i("testing", "Got to String Selected Assignment");
                String selected=listView.getItemAtPosition(position).toString();
                Log.i("testing", "Got to onGameSelected Call");
                listListener.onGameSelected(selected);            }
        });

        return root;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListListener) {
            listListener = (ListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ListListener");
        }
    }

}
