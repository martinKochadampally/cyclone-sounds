package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
    private Spinner approvalTypeSpinner;
    private String currentUsername; // Variable to safely hold the username
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
        approvalTypeSpinner = findViewById(R.id.approval_type_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.approval_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        approvalTypeSpinner.setAdapter(adapter);

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
                    String genreString = genre.getText().toString();
                    String approvalType = approvalTypeSpinner.getSelectedItem().toString();
                    createJamRequest(currentUsername, jamNameString, genreString, approvalType);
            }
        });
    }
    private void createJamRequest(final String user, final String jamName, final String genre, final String approvalType) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_STRING_REQ + "/" + currentUsername + "/" + jamName + "/" + approvalType,
                response -> {
                    Log.d("Volley Response", response);
                    Toast.makeText(getApplicationContext(), "Jam Created Successfully!", Toast.LENGTH_LONG).show();

                    // On success, navigate to the newly created jam's individual page.
                    Intent intent = new Intent(CreateJamActivity.this, IndividualJamActivity.class);
                    intent.putExtra("LOGGED_IN_USERNAME", user);
                    intent.putExtra("JAM_NAME", jamName);
                    intent.putExtra("JAM_ADMIN", currentUsername);
                    intent.putExtra("APPROVAL_TYPE", approvalType);
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
                params.put("approvalType", approvalType);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}