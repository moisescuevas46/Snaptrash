package com.example.student.snaptrash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Camera extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CameraFragment camera = new CameraFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, camera).commit();
    }
}
