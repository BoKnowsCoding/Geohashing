package fsu.mobile.group1.geohashing;


import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
   private Button home;

    public themeFragment() {
        // Required empty public constructor
    }

    public interface ThemeListener{
        public void reloadTheme();
        public void toolbarChange(String color);
        public void home();
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
        home=root.findViewById(R.id.home_id);
        home.setOnClickListener(this);
        apply.setOnClickListener(this);

/*
       tools=root.findViewById(R.id.action_bar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(tools);
       ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
        tools.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
*/


        return root;
    }

    @Override
    public void onClick(View v){

        View root=v.getRootView();

        Window window;


if(v==apply) {
    if (dark.isChecked()) {
        getContext().setTheme(R.style.Blackout_Base);
        dark.setChecked(false);
        root.setBackgroundColor(getResources().getColor(R.color.foreground_materiallight));

        if (getActivity().getWindow() != null) {
            window = getActivity().getWindow();
            themeListener.toolbarChange("Dark");
            themeListener.reloadTheme();
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
    } else if (green.isChecked()) {
        getContext().setTheme(R.style.Green_Base);
        green.setChecked(false);

        root.setBackgroundColor(getResources().getColor(R.color.colorBackground));
        if (getActivity().getWindow() != null) {
            window = getActivity().getWindow();
            themeListener.toolbarChange("Green");
            themeListener.reloadTheme();

            window.setStatusBarColor(getResources().getColor(R.color.primary_materiallight));

        }

    } else if (blue.isChecked()) {
        getContext().setTheme(R.style.Blue_Base);
        blue.setChecked(false);
        root.setBackgroundColor(getResources().getColor(R.color.colorBackground));

        if (getActivity().getWindow() != null) {
            window = getActivity().getWindow();
            themeListener.toolbarChange("Blue");
            themeListener.reloadTheme();

            window.setStatusBarColor(getResources().getColor(R.color.holo_blueDark));

        }

    } else if (red.isChecked()) {
        getContext().setTheme(R.style.Red_Base);
        red.setChecked(false);
        root.setBackgroundColor(getResources().getColor(R.color.colorBackground));

        if (getActivity().getWindow() != null) {
            window = getActivity().getWindow();
            themeListener.toolbarChange("Red");
            themeListener.reloadTheme();
            window.setStatusBarColor(getResources().getColor(R.color.primary_materialdark));

        }


    }
}
        else if(v==home){
        themeListener.home();
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
