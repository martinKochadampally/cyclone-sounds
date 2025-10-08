package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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


public class CreateActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextView usernameText;  // define username textview variable
    private Button profileButton;    // define profile button variable
    private Button homeButton;      // define music button variable
    private Button musicButton;
    private Button jamsButton;
    private EditText songName;
    private EditText artistName;
    private EditText rating;
    private EditText description;
    private Button submitButton;
    private static final String URL_STRING_REQ = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);             // link to Main activity XML

        /* initialize UI elements */
        homeButton = findViewById(R.id.home_button_btn);    // link to music button in the Main activity XML)
        profileButton = findViewById(R.id.profile_button_btn);// link to profile button in the Main activity XML
        musicButton = findViewById(R.id.music_button_btn);
        jamsButton = findViewById(R.id.jams_button_btn);
        songName = findViewById(R.id.song_name_edt);
        artistName = findViewById(R.id.arist_name_edt);
        rating = findViewById(R.id.rating_edt);
        description = findViewById(R.id.description_edt);
        submitButton = findViewById(R.id.submit_btn);

        final String URL_STRING_REQ = "";


        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            // set username text invisible initially
        } else {


        }

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateActivity.this, HomeActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateActivity.this, ProfileActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateActivity.this, MusicActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        jamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateActivity.this, JamsActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (songName.getText().toString().isEmpty() || artistName.getText().toString().isEmpty() || rating.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_LONG).show();
                    return;
                } else if (Double.parseDouble(rating.getText().toString()) > 5 || Double.parseDouble(rating.getText().toString()) < 0) {
                    Toast.makeText(getApplicationContext(), "Rating must be between 0 and 5", Toast.LENGTH_LONG).show();
                    return;
                }
                makePostRequest(extras.getString("USERNAME"), songName.getText().toString(), artistName.getText().toString(), rating.getText().toString(), description.getText().toString());
            }
        });
    }

    private void makePostRequest(final String user, final String songName, final String artistName, final String rating, final String description) {
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

                params.put("reviewer", user);
                params.put("songName", songName);
                params.put("artistName", artistName);
                params.put("rating", rating);
                params.put("description", description);

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