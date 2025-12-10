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


public class CreateJamActivity extends AppCompatActivity {

    private Button backButton;
    private Button submitButton;
    private EditText jamName;
    private EditText genre;
    private Spinner approvalTypeSpinner;
    private String currentUsername; // Variable to safely hold the username
    private String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/api/jams";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createjam);

        backButton = findViewById(R.id.back_btn);
        jamName = findViewById(R.id.jam_name_edt);
        genre = findViewById(R.id.genre_edt);
        submitButton = findViewById(R.id.submit_btn);
        approvalTypeSpinner = findViewById(R.id.approval_type_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.approval_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        approvalTypeSpinner.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("LOGGED_IN_USERNAME");
        }
        backButton.setOnClickListener(view -> {
            Intent musicIntent = new Intent(CreateJamActivity.this, JamsActivity.class);
            musicIntent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            startActivity(musicIntent);
        });

        submitButton.setOnClickListener(view -> {
            if (jamName.getText().toString().isEmpty() || genre.getText().toString().isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
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