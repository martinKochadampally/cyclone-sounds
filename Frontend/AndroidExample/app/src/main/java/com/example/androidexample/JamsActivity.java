package com.example.androidexample;

import android.content.Intent;
import android.net.Uri;
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

public class JamsActivity extends AppCompatActivity {

    private Button profileButton;
    private Button homeButton;
    private Button musicButton;
    private Button createButton;
    private Button createJamButton;
    private TableLayout jamsTable;

    private final String server = "http://coms-3090-008.class.las.iastate.edu:8080/api/jams/";
    private final String URL_GET_ACCOUNT_TYPE = "http://coms-3090-008.class.las.iastate.edu:8080/credentials/";

    interface AccountTypeCallback {
        void onResult(String accountType);
    }
    private Button friendsButton;

    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jams);

        homeButton = findViewById(R.id.home_button_btn);
        profileButton = findViewById(R.id.profile_button_btn);
        musicButton = findViewById(R.id.music_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        createJamButton = findViewById(R.id.create_jam_btn);
        jamsTable = findViewById(R.id.jams_table);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("USERNAME");

            if (currentUsername != null) {
                getAccountType(currentUsername);
                getJams(currentUsername);
            } else {
                handleNoUsername();
            }
        } else {
            handleNoUsername();
        }

        homeButton.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, HomeActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });

        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, ProfileActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });

        musicButton.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, MusicActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });

        createButton.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, CreateActivity.class);
            intent.putExtra("USERNAME", currentUsername);
            startActivity(intent);
        });
    }

    private void handleNoUsername() {
        Toast.makeText(this, "User not found, returning to login.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(JamsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void getAccountType(final String username) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_GET_ACCOUNT_TYPE + username,
                null,
                response -> {
                    try {
                        String accountType = response.getString("accountType");
                        Log.d("JamsActivity", "Account Type: " + accountType);
                        if (accountType.equals("jamManager") || accountType.equals("admin")) {
                            createJamButton.setVisibility(View.VISIBLE);
                            createJamButton.setOnClickListener(view -> {
                                Intent intent = new Intent(JamsActivity.this, CreateJamActivity.class);
                                intent.putExtra("USERNAME", username);
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
                            addJam(jamName, numParticipants, admin, username);
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

    private void addJam(String name, String numParticipants, String admin, String username) {
        TableRow newRow = new TableRow(this);

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setPadding(8, 8, 8, 8);
        nameView.setGravity(Gravity.START);
        nameView.setClickable(true);
        nameView.setOnClickListener(view -> {
            Intent intent = new Intent(JamsActivity.this, IndividualJamActivity.class);
            intent.putExtra("USERNAME", username);
            intent.putExtra("JAM_NAME", name);
            intent.putExtra("JAM_ADMIN", admin);
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

        newRow.addView(nameView);
        newRow.addView(participantsView);
        newRow.addView(adminView);

        jamsTable.addView(newRow);
    }

    private void clearTable() {
        int childCount = jamsTable.getChildCount();
        if (childCount > 1) {
            jamsTable.removeViews(1, childCount - 1);
        }
    }
}