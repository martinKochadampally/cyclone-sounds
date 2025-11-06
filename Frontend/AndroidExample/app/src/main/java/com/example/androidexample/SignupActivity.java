package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
    private EditText adminPasswordEditText;
    private Button loginButton;
    private Button signupButton;
    private Spinner accountTypeSpinner;
    private LinearLayout adminPasswordStuff;

    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/credentials";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.signup_username_edt);
        passwordEditText = findViewById(R.id.signup_password_edt);
        confirmEditText = findViewById(R.id.signup_confirm_edt);
        adminPasswordEditText = findViewById(R.id.admin_password_edt);
        loginButton = findViewById(R.id.signup_login_btn);
        signupButton = findViewById(R.id.signup_signup_btn);
        adminPasswordStuff = findViewById(R.id.admin_password_layout);

        accountTypeSpinner = findViewById(R.id.account_type_spinner);
        String[] accountTypes = new String[]{"regular", "admin", "jamManager"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountTypeSpinner.setAdapter(adapter);


        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirm = confirmEditText.getText().toString().trim();
            String accountType = accountTypeSpinner.getSelectedItem().toString();


            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.equals(confirm)) {
                if (accountType.equals("admin")) {
                    String adminPassword = adminPasswordEditText.getText().toString().trim();
                    if (adminPassword.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Admin password cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!adminPassword.equals("admin")) {
                        Toast.makeText(getApplicationContext(), "Incorrect admin password", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        makePostRequest(username, password, accountType);
                    }
                } else {
                    makePostRequest(username, password, accountType);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            }
        });
        accountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, android.view.View selectedItemView, int position, long id) {
                String selectedAccountType = parentView.getItemAtPosition(position).toString();
                // Do something with the selected account type
                if (selectedAccountType.equals("admin")) {
                    adminPasswordStuff.setVisibility(LinearLayout.VISIBLE);
                } else {
                    adminPasswordStuff.setVisibility(LinearLayout.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }

        });
    }

    private void makePostRequest(final String username, final String password, final String accountType) {
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
                            finish();
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
                params.put("username", username);
                params.put("password", password);
                params.put("accountType", accountType);
                return params;
            }
        };

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}