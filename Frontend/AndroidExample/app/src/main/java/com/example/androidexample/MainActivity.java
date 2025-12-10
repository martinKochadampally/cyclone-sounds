package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * The main entry point of the application. This activity serves as the initial screen,
 * presenting the user with options to either log in or sign up.
 */
public class MainActivity extends AppCompatActivity {

    // UI elements
    private TextView messageText;   // Displays the application name or a welcome message.
    private Button loginButton;     // Button to navigate to the Login screen.
    private Button signupButton;    // Button to navigate to the Signup screen.

    /**
     * Called when the activity is first created. This is where you should do all of your normal
     * static set up: create views, bind data to lists, etc. This method also provides you with
     * a Bundle containing the activity's previously frozen state, if there was one.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the layout for this activity.

        /* initialize UI elements */
        messageText = findViewById(R.id.main_msg_txt);      // Link to the TextView in the layout.
        loginButton = findViewById(R.id.main_login_btn);    // Link to the login button in the layout.
        signupButton = findViewById(R.id.main_signup_btn);  // Link to the signup button in the layout.

        // Set the main title of the application.
        messageText.setText("Cyclone Sounds");

        /* Set a click listener for the login button. */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* When the login button is pressed, create an Intent to switch to the LoginActivity. */
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        /* Set a click listener for the signup button. */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* When the signup button is pressed, create an Intent to switch to the SignupActivity. */
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
