package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TableRow;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONException;
import org.json.JSONObject;

/*

1. To run this project, open the directory "Android Example", otherwise it may not recognize the file structure properly

2. Ensure you are using a compatible version of gradle, to do so you need to check 2 files.

    AndroidExample/Gradle Scripts/build.gradle
    Here, you will have this block of code. Ensure it is set to a compatible version,
    in this case 8.12.2 should be sufficient:
        plugins {
            id 'com.android.application' version '8.12.2' apply false
        }

    Gradle Scripts/gradle-wrapper.properties

3. This file is what actually determines the Gradle version used, 8.13 should be sufficient.
    "distributionUrl=https\://services.gradle.org/distributions/gradle-8.13-bin.zip" ---Edit the version if needed

4. You might be instructed by the plugin manager to upgrade plugins, accept it and you may execute the default selected options.

5. Press "Sync project with gradle files" located at the top right of Android Studio,
   once this is complete you will be able to run the app

   This version is compatible with both JDK 17 and 21. The Java version you want to use can be
   altered in Android Studio->Settings->Build, Execution, Deployment->Build Tools->Gradle

 */


public class JamsActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextView usernameText;  // define username textview variable
    private Button profileButton;    // define profile button variable
    private Button homeButton;      // define music button variable
    private Button musicButton;
    private Button createButton;
    private TableLayout jamsTable;

    private String server = "http://coms-3090-008.class.las.iastate.edu:8080/jam/";
    private String URL_GET = "http://coms-3090-008.class.las.iastate.edu:8080/jams";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jams);             // link to Main activity XML

        /* initialize UI elements */
        homeButton = findViewById(R.id.home_button_btn);    // link to music button in the Main activity XML)
        profileButton = findViewById(R.id.profile_button_btn);// link to profile button in the Main activity XML
        musicButton = findViewById(R.id.music_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        jamsTable = findViewById(R.id.jams_table);

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            // set username text invisible initially
        } else {


        }

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JamsActivity.this, HomeActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JamsActivity.this, ProfileActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JamsActivity.this, MusicActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(JamsActivity.this, CreateActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });
    }

    private void getJams(final String username) {
        clearTable();

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_GET,
                null,
                response -> {
                    Log.d("Volley Response", "Received " + response.length() + " jams.");
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jamObject = response.getJSONObject(i);
                            String jamName = jamObject.optString("name", "N/A");
                            String numParticipants = jamObject.optString("numParticipants", "N/A");
                            String status = jamObject.optString("status", "N/A");
                            addJam(jamName, numParticipants, status, username);
                            Log.d("Volley Response", "Jam name: " + jamName);
                            Log.d("Volley Response", "Number of participants: " + numParticipants);
                            Log.d("Volley Response", "Status: " + status);
                        }
                    } catch (JSONException e) {
                        Log.e("Volley JSON Error", "Error parsing JSON array: " + e.getMessage());
                        Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", "Error fetching reviews: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Could not load reviews", Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }
    private void addJam(String name, String numParticipants, String status, String username) {
        TableRow newRow = new TableRow(this);

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setPadding(8, 8, 8, 8);
        nameView.setGravity(Gravity.START);
        nameView.setClickable(true);
        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                    String serverUrl = server + nameView.getText().toString();

                    WebSocketManager.getInstance().connectWebSocket(serverUrl);
                    Intent intent = new Intent(JamsActivity.this, IndividualJamActivity.class);
                    intent.putExtra("USERNAME", username);  // key-value to pass to the MainActivity
                    startActivity(intent);
                }
        });

        TextView participantsView = new TextView(this);
        participantsView.setText(numParticipants);
        participantsView.setPadding(8, 8, 8, 8);
        participantsView.setGravity(Gravity.START);

        TextView statusView = new TextView(this);
        statusView.setText(status);
        statusView.setPadding(8, 8, 8, 8);
        statusView.setGravity(Gravity.START);

        newRow.addView(nameView);
        newRow.addView(participantsView);
        newRow.addView(statusView);

        jamsTable.addView(newRow);
    }

    private void clearTable() {
        int childCount = jamsTable.getChildCount();
        if (childCount > 1) {
            jamsTable.removeViews(1, childCount - 1);
        }
    }
}

