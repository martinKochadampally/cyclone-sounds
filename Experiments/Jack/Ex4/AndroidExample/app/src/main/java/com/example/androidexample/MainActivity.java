package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView messageText;
    private TextView usernameText;
    private Button loginButton;
    private Button signupButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);
        usernameText = findViewById(R.id.main_username_txt);
        loginButton = findViewById(R.id.main_login_btn);
        signupButton = findViewById(R.id.main_signup_btn);
        logoutButton = findViewById(R.id.main_logout_btn);

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            messageText.setText("Home Page");
            usernameText.setVisibility(View.INVISIBLE);
            // Ensure logout button is gone in the initial state
            logoutButton.setVisibility(View.GONE);
        } else {
            messageText.setText("Welcome");
            usernameText.setText(extras.getString("USERNAME"));
            loginButton.setVisibility(View.INVISIBLE);
            signupButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        }

        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        /* click listener on logout button pressed */
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reset the screen to its original state
                messageText.setText("Home Page");
                usernameText.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.VISIBLE);
                logoutButton.setVisibility(View.GONE); // Hide the logout button again
            }
        });
    }
}