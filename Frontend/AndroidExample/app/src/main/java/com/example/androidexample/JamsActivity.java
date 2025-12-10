package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Activity for displaying and managing "Jams".
 * Jams are collaborative spaces, likely related to music or playlists.
 * This screen shows a list of available jams and allows users with appropriate
 * permissions to create new ones.
 */
public class JamsActivity extends AppCompatActivity {

    // UI Elements
    private Button profileButton;
    private Button homeButton;
    private Button musicButton;
    private Button createButton;
    private Button createJamButton;
    private TableLayout jamsTable;

    // Server URLs for API endpoints.
    private final String server = "http://coms-3090-008.class.las.iastate.edu:8080/api/jams";
    private final String URL_GET_ACCOUNT_TYPE = "http://coms-3090-008.class.las.iastate.edu:8080/credentials/";
    private static final String PLAYLISTS_URL = "http://coms-3090-008.class.las.iastate.edu:8080/api/playlists/";

    /**
     * (Unused) Callback interface for retrieving the account type.
     */
    interface AccountTypeCallback {
        void onResult(String accountType);
    }

    // User-specific data
    private String currentUsername;
    private String accountType;

    /**
     * Called when the activity is first created. Initializes UI, fetches user data,
     * and populates the list of jams.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jams);

        // Initialize UI components.
        homeButton = findViewById(R.id.home_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        createJamButton = findViewById(R.id.create_jam_btn);
        jamsTable = findViewById(R.id.jams_table);

        // Retrieve the logged-in username from the intent.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("LOGGED_IN_USERNAME");

            if (currentUsername != null) {
                // Fetch user and jam data.
                getAccountType(currentUsername);
                getJams(currentUsername);
            } else {
                // Handle cases where the username is not available.
                handleNoUsername();
            }
        } else {
            handleNoUsername();
        }

        // Setup navigation button listeners.
        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, HomeActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(intent);
        });

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, ProfileActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            intent.putExtra("PROFILE_TO_VIEW", currentUsername);
            startActivity(intent);
        });

        musicButton.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, MusicActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(intent);
        });

        createButton.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, CreateActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(intent);
        });
    }

    /**
     * Handles the scenario where no username is provided to the activity.
     * It shows a toast message and redirects the user to the LoginActivity.
     */
    private void handleNoUsername() {
        Toast.makeText(this, "User not found, returning to login.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(JamsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Fetches the account type for the given user from the server.
     * Based on the account type, it determines whether to show the "Create Jam" button.
     *
     * @param username The username of the current user.
     */
    private void getAccountType(final String username) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_GET_ACCOUNT_TYPE + username,
                null,
                response -> {
                    try {
                        String accountType = response.getString("accountType");
                        Log.d("JamsActivity", "Account Type: " + accountType);
                        this.accountType = accountType;
                        // Show "Create Jam" button only for jam managers and admins.
                        if (accountType.equals("jamManager") || accountType.equals("admin")) {
                            createJamButton.setVisibility(View.VISIBLE);
                            createJamButton.setOnClickListener(view -> {
                                Intent intent = new Intent(JamsActivity.this, CreateJamActivity.class);
                                intent.putExtra("LOGGED_IN_USERNAME", username);
                                startActivity(intent);
                            });
                        } else {
                            createJamButton.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("JamsActivity", "Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Could not load account type", Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Fetches the list of all jams from the server and populates the table.
     *
     * @param username The username of the current user (used for context).
     */
    private void getJams(final String username) {
        clearTable();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                server,
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jamObject = response.getJSONObject(i);
                            String jamName = jamObject.optString("name", "N/A");
                            String numParticipants = jamObject.optString("membersSize");
                            String admin = jamObject.optString("manager", "N/A");
                            String approvalType = jamObject.optString("approvalType", "Manager"); // Default to manager
                            addJam(jamName, numParticipants, admin, username, approvalType);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Could not load jams", Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void addJam(String name, String numParticipants, String admin, String username, String approvalType) {
        TableRow newRow = new TableRow(this);

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setPadding(8, 8, 8, 8);
        nameView.setGravity(Gravity.START);
        nameView.setClickable(true);
        // Clicking on a jam name navigates to its individual page.
        nameView.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, IndividualJamActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", username);
            intent.putExtra("JAM_NAME", name);
            intent.putExtra("JAM_ADMIN", admin);
            intent.putExtra("APPROVAL_TYPE", approvalType);
            startActivity(intent);
        });

        TextView participantsView = new TextView(this);
        participantsView.setText(numParticipants);
        participantsView.setPadding(8, 8, 8, 8);
        participantsView.setGravity(Gravity.START);

        TextView adminView = new TextView(this);
        adminView.setText(admin);
        adminView.setPadding(8, 8, 8, 8);
        adminView.setGravity(Gravity.START);

        TextView approvalView = new TextView(this);
        approvalView.setText(approvalType);
        approvalView.setPadding(8, 8, 8, 8);
        approvalView.setGravity(Gravity.START);

        newRow.addView(nameView);
        newRow.addView(participantsView);
        newRow.addView(adminView);
        newRow.addView(approvalView);

        // Add a delete button if the current user is the jam's admin or a site-wide admin.
        if (admin.equals(username) || (this.accountType != null && this.accountType.equals("admin"))) {
            Button deleteButton = new Button(this);
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(view -> {
                deleteJam(name);
            });
            newRow.addView(deleteButton);
        }

        jamsTable.addView(newRow);
    }


    /**
     * Deletes a jam from the server.
     *
     * @param jamName The name of the jam to be deleted.
     */
    private void deleteJam(String jamName) {
        // Request to delete the jam itself.
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE, server + "/" + jamName,
                response -> {
                    Toast.makeText(JamsActivity.this, "Jam deleted", Toast.LENGTH_SHORT).show();
                    getJams(currentUsername); // Refresh the list of jams.
                },
                error -> {
                    Log.e("JamsActivity", "Deletion Error: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Failed to delete jam", Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

        // Request to delete the playlist associated with the jam.
        String url = PLAYLISTS_URL + currentUsername + "/" + jamName;

        StringRequest stringRequestPlaylist = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    Log.d("Delete Response", response);
                    Toast.makeText(JamsActivity.this, "Playlist associated deleted", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Log.e("Delete Error", error.toString());
                    Toast.makeText(JamsActivity.this, "Delete failed", Toast.LENGTH_SHORT).show();
                });
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestPlaylist);
    }

    /**
     * Clears all jam entries from the table, except for the header row.
     */
    private void clearTable() {
        int childCount = jamsTable.getChildCount();
        if (childCount > 1) {
            jamsTable.removeViews(1, childCount - 1);
        }
    }
}