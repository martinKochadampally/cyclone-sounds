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

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    private static final String URL_LOGIN_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/login";

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
        String urlWithParams = Uri.parse(URL_LOGIN_REQ)
                .buildUpon()
                .appendQueryParameter("username", username)
                .appendQueryParameter("password", password)
                .build().toString();

        Log.d("Login URL", "Requesting URL: " + urlWithParams);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                response -> {
                    Log.d("Volley Response", "Server responded with: " + response);

                    if (response.trim().equalsIgnoreCase("true")) {
                        Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("LOGGED_IN_USERNAME", username);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Login Failed: Invalid username or password", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Error: Could not connect to server", Toast.LENGTH_LONG).show();
                });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}