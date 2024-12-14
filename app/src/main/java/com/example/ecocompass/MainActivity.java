package com.example.ecocompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Account Button
        Button buttonAccount = findViewById(R.id.buttonAccount);
        buttonAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, accountActivity.class); // Ensure correct class name
            startActivity(intent);
        });

        // Scanner Button

        //List Button
        Button buttonList = findViewById(R.id.buttonList);
        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        //GPS Button
        Button buttonGPS = findViewById(R.id.buttonGPS);
        buttonGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, gps_locator.class);
                startActivity(intent);
            }
        });


    }
}
