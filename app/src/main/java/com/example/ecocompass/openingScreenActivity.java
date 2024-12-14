package com.example.ecocompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class openingScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_page);

        Button button = findViewById(R.id.buttonStart);

        //Opening App from Splash Screen
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(openingScreenActivity.this, loginInActivity.class);
                startActivity(intent);
            }
        });
    }
}