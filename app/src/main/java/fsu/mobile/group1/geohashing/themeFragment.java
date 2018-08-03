package fsu.mobile.group1.geohashing;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class themeFragment extends Fragment implements View.OnClickListener {


   private RadioButton dark;
   private RadioButton green;
   private RadioButton blue;
   private RadioButton red;
   private Button apply;
   private ThemeListener themeListener;


    public themeFragment() {
        // Required empty public constructor
    }

    public interface ThemeListener{
        public void reloadTheme();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_theme, container, false);
        dark=root.findViewById(R.id.dark_id);
        green=root.findViewById(R.id.green_id);
        blue=root.findViewById(R.id.blue_id);
        red=root.findViewById(R.id.red_id);
        apply=root.findViewById(R.id.apply_id);
        apply.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v){

        View root=v.getRootView();

        if(dark.isChecked()){
            Log.i("dark", "dark theme selected.............................");
        getContext().setTheme(R.style.Blackout_Base);
        dark.setChecked(false);
        root.setBackgroundColor(getResources().getColor(R.color.foreground_materiallight));
        themeListener.reloadTheme();
        }
        else if(green.isChecked()){
            getContext().setTheme(R.style.Green_Base);
            green.setChecked(false);
            root.setBackgroundColor(getResources().getColor(R.color.colorBackground));
            themeListener.reloadTheme();

        }
        else if (blue.isChecked()){
        getContext().setTheme(R.style.Blue_Base);
        blue.setChecked(false);
            root.setBackgroundColor(getResources().getColor(R.color.colorBackground));

            themeListener.reloadTheme();

        }
        else if (red.isChecked()){
        getContext().setTheme(R.style.Red_Base);
        red.setChecked(false);
            root.setBackgroundColor(getResources().getColor(R.color.colorBackground));


            themeListener.reloadTheme();

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ThemeListener) {
            themeListener = (ThemeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ThemeListener");
        }

    }


}
