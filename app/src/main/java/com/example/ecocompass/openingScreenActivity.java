package com.example.ecocompass;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class openingScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        Button startButton = findViewById(R.id.buttonStart);

        startButton.setOnClickListener(v -> {

        });

    }
}