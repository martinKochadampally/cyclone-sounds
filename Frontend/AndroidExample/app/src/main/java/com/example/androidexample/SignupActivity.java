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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private Button loginButton;
    private Button signupButton;

    // Your specific IP address is now included.
    private static final String URL_STRING_REQ = "http://10.48.116.71:8080/credentials";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.signup_username_edt);
        passwordEditText = findViewById(R.id.signup_password_edt);
        confirmEditText = findViewById(R.id.signup_confirm_edt);
        loginButton = findViewById(R.id.signup_login_btn);
        signupButton = findViewById(R.id.signup_signup_btn);

        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirm = confirmEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.equals(confirm)) {
                makePostRequest(username, password);
            } else {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makePostRequest(final String username, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_STRING_REQ,
                response -> {
                    Log.d("Volley Response", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message");

                        if (message.equals("success")) {
                            Toast.makeText(getApplicationContext(), "Signup Successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Close signup activity
                        } else {
                            Toast.makeText(getApplicationContext(), "Signup Failed: User might already exist", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.e("Volley JSON Error", "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Volley Error: Could not connect to server", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emailId", username + "@iastate.edu"); // Assuming a default domain
                params.put("username", username);
                params.put("password", password);
                params.put("accountType", "regular");
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}