package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    // The URL for your backend's login endpoint
    private static final String URL_LOGIN_REQ = "http://10.48.116.71:8080/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);
        signupButton = findViewById(R.id.login_signup_btn);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!username.isEmpty() && !password.isEmpty()) {
                makeLoginRequest(username, password);
            } else {
                Toast.makeText(getApplicationContext(), "Please enter username and password", Toast.LENGTH_SHORT).show();
            }
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void makeLoginRequest(final String username, final String password) {
        // Build the URL with the username and password as query parameters
        String urlWithParams = URL_LOGIN_REQ + "?username=" + username + "&password=" + password;

        // Create the GET request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                response -> {
                    Log.d("Volley Response", "Server responded with: " + response);

                    // Check if the response from the server is literally "true"
                    if (response.equalsIgnoreCase("true")) {
                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                        // Navigate to HomeActivity on success
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish(); // Close the login activity so the user can't go back to it
                    } else {
                        Toast.makeText(getApplicationContext(), "Login Failed: Invalid username or password", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Error: Could not connect to server", Toast.LENGTH_LONG).show();
                });

        // Add the request to the Volley queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}