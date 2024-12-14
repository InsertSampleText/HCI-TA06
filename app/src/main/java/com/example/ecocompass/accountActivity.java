package com.example.ecocompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;


public class accountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page);

        ImageButton backButton = findViewById(R.id.backArrowAccount);
        Button logOutButton = findViewById(R.id.buttonLogOut);

        //Back Arrow Functionality
        backButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        //Logging Out
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(accountActivity.this, openingScreenActivity.class);
                startActivity(intent);
            }
        });

    }
}
