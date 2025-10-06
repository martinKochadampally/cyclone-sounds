package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;
import org.json.JSONException;


import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable
    private EditText confirmEditText;   // define confirm edittext variable
    private Button loginButton;         // define login button variable
    private Button signupButton;        // define signup button variable
    private TextView msgResponse;

    // API for Volley
    private static final String URL_STRING_REQ = "http://localhost:8080/credentials";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /* initialize UI elements */
        usernameEditText = findViewById(R.id.signup_username_edt);  // link to username edtext in the Signup activity XML
        passwordEditText = findViewById(R.id.signup_password_edt);  // link to password edtext in the Signup activity XML
        confirmEditText = findViewById(R.id.signup_confirm_edt);    // link to confirm edtext in the Signup activity XML
        loginButton = findViewById(R.id.signup_login_btn);    // link to login button in the Signup activity XML
        signupButton = findViewById(R.id.signup_signup_btn);  // link to signup button in the Signup activity XML

        msgResponse = findViewById(R.id.msg_Response);

        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when login button is pressed, use intent to switch to Login Activity */
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);  // go to LoginActivity
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirm = confirmEditText.getText().toString();

                if (password.equals(confirm)){
                    Toast.makeText(getApplicationContext(), "Signing up", Toast.LENGTH_LONG).show();
                    makePostRequest(username, password);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Password don't match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void makePostRequest(final String username, final String password) {
        // Creating a new String request
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST, // HTTP method (POST request)
                URL_STRING_REQ, // API URL
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Log the response for debugging purposes
                        Log.d("Volley Response", response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String message = jsonResponse.getString("message");
                            // Display response in the TextView
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            intent.putExtra("USERNAME", username);  // key-value to pass to the MainActivity
                            intent.putExtra("PASSWORD", password);  // key-value to pass to the MainActivity
                            startActivity(intent);
                        } catch (JSONException e) {
                            Log.e("Volley JSON Error", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log the error details
                        Log.e("Volley Error", error.toString());

                        // Show an error message in the UI
                        Toast.makeText(getApplicationContext(), "Volley Error", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Headers for the request (if needed)
                Map<String, String> headers = new HashMap<>();

                // Example headers (uncomment if needed)
                // headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                // headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                // Parameters for the request (if needed)
                Map<String, String> params = new HashMap<>();

                params.put("emailId", username + "@gmail.com");
                params.put("username", username);
                params.put("password", password);
                params.put("accountType", "regular");

                // Example parameters (uncomment if needed)
                // params.put("param1", "value1");
                // params.put("param2", "value2");
                return params;
            }

        };

        // Adding request to the Volley request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}