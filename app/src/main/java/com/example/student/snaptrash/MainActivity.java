package com.example.student.snaptrash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.backendless.Backendless;

public class MainActivity extends AppCompatActivity {

    public static final String APP_ID = "E85AC4AC-ADB7-F784-FFCD-8EA3954F3300";
    public static final String SECRET_KEY = "EA54F03A-83EA-5661-FF0A-837E1566FA00";
    public static final String VERSION = "v1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //MainMenuFragment mainMenu = new MainMenuFragment();
        //getSupportFragmentManager().beginTransaction().add(R.id.container, mainMenu).commit();

        Backendless.initApp(this, APP_ID, SECRET_KEY, VERSION);
        if (Backendless.UserService.loggedInUser() == "" ){
            MainMenuFragment mainMenu = new MainMenuFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mainMenu).commit();

        }
        else {
            MainMenuFragment mainmenuFragment = new MainMenuFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, mainmenuFragment).commit();
        }
    }
}
