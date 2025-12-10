package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity allows authorized users (Jam Managers or Admins) to create a new "Jam".
 * A Jam is a collaborative space, likely for building playlists together.
 */
public class CreateJamActivity extends AppCompatActivity {

    // UI Elements
    private Button backButton;
    private Button submitButton;
    private EditText jamName;
    private EditText genre;

    // Data fields
    private String currentUsername; // Holds the username of the logged-in user.
    private String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/api/jams";


    /**
     * Called when the activity is first created. Initializes UI components and sets up
     * listeners for the back and submit buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createjam);

        // Initialize UI components from the layout.
        backButton = findViewById(R.id.back_btn);
        jamName = findViewById(R.id.jam_name_edt);
        genre = findViewById(R.id.genre_edt); // Note: Genre is collected but not used in the createJamRequest.
        submitButton = findViewById(R.id.submit_btn);

        // Retrieve the username from the intent that started this activity.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("LOGGED_IN_USERNAME");
        }

        // Set listener for the back button to return to the Jams list.
        backButton.setOnClickListener(view -> {
            Intent musicIntent = new Intent(CreateJamActivity.this, JamsActivity.class);
            musicIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(musicIntent);
        });

        // Set listener for the submit button to create the new jam.
        submitButton.setOnClickListener(view -> {
            // Basic validation to ensure fields are not empty.
            if (jamName.getText().toString().isEmpty() || genre.getText().toString().isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                // Sanitize jam name by replacing spaces with underscores.
                if (jamName.getText().toString().contains(" ")) {
                    jamName.setText(jamName.getText().toString().replace(" ", "_"));
                }
                    String jamNameString = jamName.getText().toString();
                    String genreString = genre.getText().toString(); // Genre is captured but not sent in the request.
                    createJamRequest(currentUsername, jamNameString, genreString);
            }
        });
    }

    /**
     * Makes a POST request to the server to create a new Jam.
     *
     * @param user The username of the creator (manager).
     * @param jamName The name for the new jam.
     * @param genre The genre of the new jam (currently unused in the request itself).
     */
    private void createJamRequest(final String user, final String jamName, final String genre) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_STRING_REQ + "/" + currentUsername + "/" + jamName, // The manager and jam name are part of the URL.
                response -> {
                    Log.d("Volley Response", response);
                    Toast.makeText(getApplicationContext(), "Jam Created Successfully!", Toast.LENGTH_LONG).show();

                    // On success, navigate to the newly created jam's individual page.
                    Intent intent = new Intent(CreateJamActivity.this, IndividualJamActivity.class);
                    intent.putExtra("LOGGED_IN_USERNAME", user);
                    intent.putExtra("JAM_NAME", jamName);
                    intent.putExtra("JAM_ADMIN", currentUsername);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Submission Error", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // These params are defined but may not be used if the server gets all data from the URL.
                Map<String, String> params = new HashMap<>();
                params.put("username", currentUsername);
                params.put("name", jamName);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
