package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

/**
 * Activity for handling user login. It provides fields for username and password
 * and a button to initiate the login process.
 */
public class LoginActivity extends AppCompatActivity {

    // UI elements for username, password, login, and signup.
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    // The URL for the login request on the server.
    private static final String URL_LOGIN_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/login";

    /**
     * Called when the activity is first created. Initializes the UI components
     * and sets up listeners for the login and signup buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components by finding them in the layout.
        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        signupButton = findViewById(R.id.login_signup_btn);

        // Set a click listener for the login button.
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate that both fields are filled.
            if (!username.isEmpty() && !password.isEmpty()) {
                makeLoginRequest(username, password);
            } else {
                Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
            }
        });

        // Set a click listener for the signup button to navigate to the SignupActivity.
        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Makes a GET request to the server to authenticate the user.
     *
     * @param username The username entered by the user.
     * @param password The password entered by the user.
     */
    private void makeLoginRequest(final String username, final String password) {
        // Build the URL with username and password as query parameters.
        String urlWithParams = Uri.parse(URL_LOGIN_REQ)
                .buildUpon()
                .appendQueryParameter("username", username)
                .appendQueryParameter("password", password)
                .build().toString();

        Log.d("Login URL", "Requesting URL: " + urlWithParams);

        // Create a new StringRequest.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                response -> {
                    Log.d("Volley Response", "Server responded with: " + response);

                    // Check if the server response indicates a successful login.
                    if (response.trim().equalsIgnoreCase("true")) {
                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();

                        // Navigate to the HomeActivity on successful login.
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("LOGGED_IN_USERNAME", username);
                        startActivity(intent);
                        finish(); // Finish LoginActivity so the user can't navigate back to it.
                    } else {
                        Toast.makeText(getApplicationContext(), "Login Failed: Invalid username or password", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    // Handle server connection errors.
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Error: Could not connect to server", Toast.LENGTH_LONG).show();
                });

        // Add the request to the Volley request queue.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
