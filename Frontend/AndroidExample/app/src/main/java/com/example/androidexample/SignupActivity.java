package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

/**
 * Activity for user registration. It allows a new user to create an account by providing
 * a username, password, and selecting an account type. It includes special validation
 * for creating an admin account.
 */
public class SignupActivity extends AppCompatActivity {

    // UI elements for the signup form.
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText confirmEditText;
    private EditText adminPasswordEditText;
    private Button loginButton;
    private Button signupButton;
    private Spinner accountTypeSpinner;
    private LinearLayout adminPasswordStuff; // Layout for admin-specific fields.

    // URL for the signup endpoint on the server.
    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/credentials";

    /**
     * Called when the activity is first created. Initializes UI components, sets up the
     * account type spinner, and defines button click listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components.
        usernameEditText = findViewById(R.id.signup_username_edt);
        passwordEditText = findViewById(R.id.signup_password_edt);
        confirmEditText = findViewById(R.id.signup_confirm_edt);
        adminPasswordEditText = findViewById(R.id.admin_password_edt);
        loginButton = findViewById(R.id.signup_login_btn);
        signupButton = findViewById(R.id.signup_signup_btn);
        adminPasswordStuff = findViewById(R.id.admin_password_layout);
        accountTypeSpinner = findViewById(R.id.account_type_spinner);

        // Set up the spinner for account types.
        String[] accountTypes = new String[]{"regular", "admin", "jamManager"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountTypeSpinner.setAdapter(adapter);

        // Listener to navigate to the Login screen.
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Listener for the main signup button.
        signupButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirm = confirmEditText.getText().toString().trim();
            String accountType = accountTypeSpinner.getSelectedItem().toString();

            // Basic validation.
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if passwords match.
            if (password.equals(confirm)) {
                // Special handling for admin account creation.
                if (accountType.equals("admin")) {
                    String adminPassword = adminPasswordEditText.getText().toString().trim();
                    if (adminPassword.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Admin password cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!adminPassword.equals("admin")) { // Super secure admin password check.
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

        // Listener for the account type spinner to show/hide the admin password field.
        accountTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedAccountType = parentView.getItemAtPosition(position).toString();
                if (selectedAccountType.equals("admin")) {
                    adminPasswordStuff.setVisibility(View.VISIBLE);
                } else {
                    adminPasswordStuff.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here.
            }
        });
    }

    /**
     * Makes a POST request to the server to create a new user account.
     *
     * @param username The desired username.
     * @param password The desired password.
     * @param accountType The selected account type ("regular", "admin", "jamManager").
     */
    private void makePostRequest(final String username, final String password, final String accountType) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_STRING_REQ,
                response -> {
                    Log.d("Volley Response", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        String message = jsonResponse.getString("message");

                        // Handle server response.
                        if (message.equals("success")) {
                            Toast.makeText(getApplicationContext(), "Signup Successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Finish this activity to prevent user from coming back.
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
                // POST parameters to be sent with the request.
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("accountType", accountType);
                return params;
            }
        };

        // Add the request to the Volley queue.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
