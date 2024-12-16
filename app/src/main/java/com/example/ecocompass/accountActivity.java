package com.example.ecocompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;


public class accountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_page);

        ImageButton backButton = findViewById(R.id.backArrowAccount);
        EditText editName = findViewById(R.id.userField);
        EditText editDate = findViewById(R.id.dateJoinedField);
        EditText editPoint = findViewById(R.id.pointsField);
        EditText editStreak = findViewById(R.id.currentStreakField);

        Button logOutButton = findViewById(R.id.buttonLogOut);
        Button saveButton = findViewById(R.id.buttonSave);
        Button editButton = findViewById(R.id.buttonEdit);

        // Save Button functionality
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName.setEnabled(false);
                editDate.setEnabled(false);
                editPoint.setEnabled(false);
                editStreak.setEnabled(false);
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
            }
        });

        // Re-Edit Button functionality
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editName.setEnabled(true);
                editDate.setEnabled(true);
                editPoint.setEnabled(true);
                editStreak.setEnabled(true);
                editButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
            }
        });

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
