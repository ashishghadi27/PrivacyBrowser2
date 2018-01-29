package com.asg.ashish.privacybrowser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static com.asg.ashish.privacybrowser.MainActivity.homepage;
import static com.asg.ashish.privacybrowser.MainActivity.searchengine;

public class Settings extends AppCompatActivity  {

    int spinnerPos=0,themespinnerpos;
    static String homestring,themesettings;
    EditText home;
    public SharedPreferences mPreferences,tPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Material_Light_DarkActionBar);
        setContentView(R.layout.activity_settings);

        final Spinner searchdefault =  findViewById(R.id.searchengines);
        home = findViewById(R.id.homeedittext);
        /*final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        final RelativeLayout layout1 = findViewById(R.id.layout1);
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();*/



        searchdefault.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String engine = parent.getItemAtPosition(position).toString();
                switch (engine) {
                    case "Google":
                        searchengine = "https://www.google.com/search?q=";
                        break;
                    case "Yahoo":
                        searchengine = "https://search.yahoo.com/search?q=";
                        break;
                    case "Bing":
                        searchengine = "https://www.bing.com/search?q=";
                        break;
                    default:
                        searchengine = "https://duckduckgo.com/?q=";
                        break;
                }
                spinnerPos = searchdefault.getSelectedItemPosition();


            }
            public void onNothingSelected(AdapterView<?> parent) {
                searchengine="https://www.google.com/search?q=";
            }

        });

        final Spinner themespinner = findViewById(R.id.themespinnner);


        themespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //MainActivity.theme = parent.getItemAtPosition(position).toString();
                themesettings = parent.getItemAtPosition(position).toString();
                MainActivity.theme = themesettings;

                themespinnerpos = themespinner.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //MainActivity.theme ="Material Dark";
                themesettings = "Material Dark";

            }
        });


        home.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                homepage = home.getText().toString();

                return true;
            }
        });



    }


    @Override
    protected void onStop() {
        //***************For Search engine*******
        super.onStop();
        mPreferences = getSharedPreferences("spinnerPos",MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("pos1",spinnerPos);
        preferencesEditor.apply();


        mPreferences = getSharedPreferences("maindata",MODE_PRIVATE);
        preferencesEditor.putString("pos2",searchengine);
        preferencesEditor.apply();

        //************************For Home Page**********************
        mPreferences = getSharedPreferences("maindata",MODE_PRIVATE);
        preferencesEditor.putString("Homepos",homepage);
        preferencesEditor.apply();

        //*************************For Theming***********************
        /**/

        SharedPreferences.Editor tpreferencesEditor = tPreferences.edit();
        tPreferences =getSharedPreferences("spinnerposfortheme",MODE_PRIVATE);
        tpreferencesEditor.putInt("themesettingsactivity",themespinnerpos);
        tpreferencesEditor.apply();

        mPreferences = getSharedPreferences("maindatafortheme",MODE_PRIVATE);
        Toast.makeText(this,themesettings,Toast.LENGTH_SHORT).show();
        preferencesEditor.putString("thememain",themesettings);
        preferencesEditor.apply();


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //************For Search Engine*******************
        mPreferences = getSharedPreferences("spinnerPos",MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("pos1",spinnerPos);
        preferencesEditor.apply();



        mPreferences = getSharedPreferences("maindata",MODE_PRIVATE);
        preferencesEditor.putString("pos2",searchengine);
        preferencesEditor.apply();




        //**************For Homepage******************
        mPreferences = getSharedPreferences("maindata",MODE_PRIVATE);
        preferencesEditor.putString("Homepos",homepage);
        preferencesEditor.apply();

        //*************************For Theming***********************
        SharedPreferences.Editor tpreferencesEditor = tPreferences.edit();
        tPreferences =getSharedPreferences("spinnerposfortheme",MODE_PRIVATE);
        tpreferencesEditor.putInt("themesettingsactivity",themespinnerpos);
        tpreferencesEditor.apply();

        tPreferences = getSharedPreferences("maindatafortheme",MODE_PRIVATE);
        tpreferencesEditor.putString("thememain",themesettings);
        tpreferencesEditor.apply();
        //themesetter();

        //Toast.makeText(this,"Restart Browser to apply selected themes",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //*******For search engine**********
        mPreferences = getSharedPreferences("spinnerPos",MODE_PRIVATE);
        int i = mPreferences.getInt("pos1",0);
        Spinner searchdefault = findViewById(R.id.searchengines);
        searchdefault.setSelection(i,true);



        //***********For Homepage**********
        mPreferences = getSharedPreferences("maindata",MODE_PRIVATE);
        homestring = mPreferences.getString("Homepos","www.google.com");
        home.setText(homestring);

        //***************For theming***************
        tPreferences = getSharedPreferences("spinnerposfortheme",MODE_PRIVATE);
        int j = tPreferences.getInt("themesettingsactivity",0);
        Log.println(j,themesettings,"This is log");
        Spinner themespinner = findViewById(R.id.themespinnner);
        themespinner.setSelection(j);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //**********For Search Engine************

        mPreferences = getSharedPreferences("spinnerPoss",MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = mPreferences.edit();
        preferencesEditor.putInt("pos1",spinnerPos);
        preferencesEditor.apply();


        mPreferences = getSharedPreferences("maindata",MODE_PRIVATE);
        preferencesEditor.putString("pos2",searchengine);
        preferencesEditor.apply();



        //**************For Homepage******************
        mPreferences = getSharedPreferences("maindata",MODE_PRIVATE);
        preferencesEditor.putString("Homepos",homepage);
        preferencesEditor.apply();

        //*************************For Theming***********************
        /*mPreferences =getSharedPreferences("spinnerposfortheme",MODE_PRIVATE);
        preferencesEditor.putInt("themesettingsactivity",themespinnerpos);
        preferencesEditor.apply();

        mPreferences = getSharedPreferences("maindatafortheme",MODE_PRIVATE);
        preferencesEditor.putString("thememain",themesettings);
        preferencesEditor.apply();*/

    }

    @Override
    protected void onResume(){
        super.onResume();
        home.setText(homepage);

    }



}
