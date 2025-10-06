package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button signupButton;

    private static final String URL_STRING_REQ = "https://7d516973-a034-410b-95cd-47eade783d4e.mock.pstmn.io/login";

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

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
                makeGetRequest(username, password);
            }
        });

        signupButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void makeGetRequest(final String username, final String password) {
        Uri.Builder builder = Uri.parse(URL_STRING_REQ).buildUpon();
        builder.appendQueryParameter("username", username);
        builder.appendQueryParameter("password", password);
        String urlWithParams = builder.build().toString();

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                urlWithParams,
                response -> {
                    Log.d("Volley Response", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.getString("status");

                        if ("success".equals(status)) {
                            String message = jsonResponse.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                            // *** THIS LINE HAS BEEN CHANGED ***
                            // It now navigates to HomeActivity upon successful login.
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

                            intent.putExtra("USERNAME", username);
                            startActivity(intent);
                            finish();
                        } else {
                            String message = jsonResponse.getString("message");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Log.e("Volley JSON Error", "Error parsing JSON: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}