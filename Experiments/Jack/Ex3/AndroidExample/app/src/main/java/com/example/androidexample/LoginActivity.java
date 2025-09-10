package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button signupButton; // 1. Add variable for signup button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize all the views using the IDs from your XML
        usernameInput = findViewById(R.id.login_username_edt);
        passwordInput = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        signupButton = findViewById(R.id.login_signup_btn); // 2. Initialize signup button


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = usernameInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                boolean fieldsAreFilled = !username.isEmpty() && !password.isEmpty();


                loginButton.setEnabled(fieldsAreFilled);
                signupButton.setEnabled(fieldsAreFilled);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // Attach the watcher to both input fields
        usernameInput.addTextChangedListener(textWatcher);
        passwordInput.addTextChangedListener(textWatcher);

        // Set up the click listener for the login button
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });

        // Set up the click listener for the signup button
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            // You can also pass the username to the signup page if you want
            // intent.putExtra("USERNAME", usernameInput.getText().toString());
            startActivity(intent);
        });
    }
}