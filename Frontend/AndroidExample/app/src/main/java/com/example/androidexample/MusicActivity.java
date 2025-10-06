package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.Toast;
import android.widget.TableRow;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

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


public class MusicActivity extends AppCompatActivity {

    private TextView messageText;   // define message textview variable
    private TextView usernameText;  // define username textview variable
    private Button profileButton;    // define profile button variable
    private Button homeButton;      // define music button variable
    private Button jamsButton;
    private Button createButton;
    private TableLayout tableLayout;
    private RequestQueue requestQueue;
    private static final String URL_STRING_REQ = "https://9e5d2bff-061b-461d-a5f5-6050e3c1616d.mock.pstmn.io/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);             // link to Main activity XML

        /* initialize UI elements */
        homeButton = findViewById(R.id.home_button_btn);    // link to music button in the Main activity XML)
        profileButton = findViewById(R.id.profile_button_btn);// link to profile button in the Main activity XML
        jamsButton = findViewById(R.id.jams_button_btn);
        createButton = findViewById(R.id.create_button_btn);
        tableLayout = findViewById(R.id.song_table);

        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        fetchMusicData();

        /* extract data passed into this activity from another activity */
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
                         // set username text invisible initially
        } else {


        }

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicActivity.this, HomeActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicActivity.this, ProfileActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        jamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicActivity.this, JamsActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicActivity.this, CreateActivity.class);
                intent.putExtra("USERNAME", extras.getString("USERNAME"));  // key-value to pass to the MainActivity
                startActivity(intent);
            }
        });
    }
    private void fetchMusicData() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET, // HTTP method (GET request)
                URL_STRING_REQ, // API URL
                null, // Request body (null for GET request)
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log response for debugging
                        Log.d("Volley Response", response.toString());
                        try {
                            // Display response in TextView
                            JSONArray songsArray = response.getJSONArray("songs");
                            for (int i = 0; i < songsArray.length(); i++) {
                                JSONObject songObject = songsArray.getJSONObject(i);
                                String user = songObject.optString("user", "N/A");
                                String title = songObject.optString("title", "N/A");
                                String artist = songObject.optString("artist", "N/A");
                                double rating = songObject.getDouble("rating");

                                TableRow tableRow = new TableRow(MusicActivity.this);
                                tableRow.addView(createTextView(user, true));
                                tableRow.addView(createTextView(title, true));
                                tableRow.addView(createTextView(artist, true));
                                tableRow.addView(createTextView(String.valueOf(rating), true));
                                tableLayout.addView(tableRow);


                            }
                        } catch (JSONException e) {
                            Log.e("Volley JSON Error", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Parsing Error", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log error details
                        Log.e("Volley Error", error.toString());

                        // Display an error message in UI
                        Toast.makeText(getApplicationContext(), "Volley Error", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                // Define headers if needed
                HashMap<String, String> headers = new HashMap<>();
                // Example headers (uncomment if needed)
                // headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                // headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                // Define parameters if needed
                Map<String, String> params = new HashMap<>();
                // Example parameters (uncomment if needed)
                // params.put("param1", "value1");
                // params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to the Volley request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjReq);
    }

    private TextView createTextView(String text, boolean useWeight) {
        TextView textView = new TextView(this);
        textView.setText(text);
        if (useWeight) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            textView.setLayoutParams(params);

        }
        return textView;
    }
    private void clearTable() {
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }
    }
}