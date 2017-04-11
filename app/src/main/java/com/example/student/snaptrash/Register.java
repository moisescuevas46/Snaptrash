package com.example.student.snaptrash;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegisterFragment register = new RegisterFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.registerContainer, register).commit();
    }
}
