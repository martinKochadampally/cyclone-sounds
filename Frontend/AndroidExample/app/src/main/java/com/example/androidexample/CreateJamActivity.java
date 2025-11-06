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


public class CreateJamActivity extends AppCompatActivity {

    private Button backButton;
    private Button submitButton;
    private EditText jamName;
    private EditText genre;
    private String currentUsername; // Variable to safely hold the username
    private String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/jams";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createjam);

        backButton = findViewById(R.id.back_btn);
        jamName = findViewById(R.id.jam_name_edt);
        genre = findViewById(R.id.genre_edt);
        submitButton = findViewById(R.id.submit_btn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("USERNAME");
        }
        backButton.setOnClickListener(view -> {
            Intent musicIntent = new Intent(CreateJamActivity.this, JamsActivity.class);
            musicIntent.putExtra("USERNAME", currentUsername);
            startActivity(musicIntent);
        });

        submitButton.setOnClickListener(view -> {
            if (jamName.getText().toString().isEmpty() || genre.getText().toString().isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                String jamNameString = jamName.getText().toString();
                String genreString = genre.getText().toString();
                createJamRequest(currentUsername, jamNameString, genreString);
            }
        });
    }
    private void createJamRequest(final String user, final String jamName, final String genre) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_STRING_REQ,
                response -> {
                    Log.d("Volley Response", response);
                    Toast.makeText(getApplicationContext(), "Jam Created Successfully!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CreateJamActivity.this, IndividualJamActivity.class);
                    intent.putExtra("USERNAME", user);
                    intent.putExtra("JAM_NAME", jamName);
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
                Map<String, String> params = new HashMap<>();
                params.put("admin", currentUsername);
                params.put("jamName", jamName);
                params.put("genre", genre);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}