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

        // Set the content view first
        setContentView(R.layout.activity_main);

        // Enable edge-to-edge for system bars
        EdgeToEdge.enable(this);

        // Account Button
        Button buttonAccount = findViewById(R.id.buttonAccount);
        buttonAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, account_page.class); // Ensure correct class name
            startActivity(intent);
        });

        // GPS Button
        Button buttonGPS = findViewById(R.id.buttonGPS); // Uncomment or add this button in your XML
        buttonGPS.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, gps_locator.class); // Ensure correct class name
            startActivity(intent);
        });

        // Apply window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
