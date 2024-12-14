package com.example.ecocompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class loginInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_in_page);

        Button loginInButton = findViewById(R.id.buttonLoginIn);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        //'Logging' in and going to Home Screen
        loginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //Take User to 'Sign' Up
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginInActivity.this, signUpActivity.class);
                startActivity(intent);
            }
        });
    }
}