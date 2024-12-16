package com.example.ecocompass;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class loginInActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_in_page);

        EditText emailEdit = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEdit = findViewById(R.id.editPasswordLogin);

        Button loginInButton = findViewById(R.id.buttonLoginIn);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        //'Logging' in and going to Home Screen
        loginInButton.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            if (!isValidEmail(email)) {
                emailEdit.setError("Enter a valid email address, please");
                return;
            }

            // Simple 'Authentication' for Proof of Concept
            if (email.equals("user@example.com") && password.equals("password")) {
                Toast.makeText(loginInActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(loginInActivity.this, MainActivity.class);
                startActivity(intent);

            } else {
                Toast.makeText(loginInActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
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

    //Ensure User's Input for Email is Valid
    private boolean isValidEmail(CharSequence email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}