package com.example.ecocompass;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;


public class accountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page);

        ImageButton backButton = findViewById(R.id.backArrowAccount);

        backButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

    }
}
