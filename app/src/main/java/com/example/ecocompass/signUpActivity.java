package com.example.ecocompass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

public class signUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);

        EditText emailEdit = findViewById(R.id.emailAddressSignUp);
        EditText passwordEdit = findViewById(R.id.editPasswordSignUp);
        EditText confirmPassword = findViewById(R.id.editPasswordConfirmSignUp);

        Button createButton = findViewById(R.id.buttonCreateAccount);
        ImageButton backButton = findViewById(R.id.backArrowSignUp);

        //'Creating' an Account and going to Home Screen
        createButton.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            //Minor Error-Handling for the fields
            if (!isValidEmail(email)) {
                emailEdit.setError("Enter a valid email address");
                return;
            }

            if (password.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(signUpActivity.this, "Password fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPass)) {
                confirmPassword.setError("Passwords do not match");
                return;
            }

            startActivity(new Intent(signUpActivity.this, MainActivity.class));
        });

        //Back Arrow Functionality
        backButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });
    }

    //Checks that Email is a Valid Format
    private boolean isValidEmail(CharSequence email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}