package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CreatePlaylistActivity extends AppCompatActivity {

    private Button backButton;
    private EditText playlistName;
    private Button submitButton;
    private String currentUsername;

    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/playlists/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);

        backButton = findViewById(R.id.back_btn);
        playlistName = findViewById(R.id.playlist_name_edt);
        submitButton = findViewById(R.id.submit_btn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUsername = extras.getString("USERNAME");
        }

        backButton.setOnClickListener(view -> navigateTo(CreateActivity.class));



        submitButton.setOnClickListener(view -> {
            String playlist = playlistName.getText().toString().trim();

            if (playlist.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Name cannot be empty", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                makePostRequest(currentUsername, playlist);
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void makePostRequest(final String user, final String playlistName) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_STRING_REQ + user,
                response -> {
                    Log.d("Volley Response", response);
                    Toast.makeText(this, "Playlist Created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreatePlaylistActivity.this, AddSongsActivity.class);
                    intent.putExtra("USERNAME", user);
                    intent.putExtra("PLAYLIST_NAME", playlistName);
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
                params.put("playlistName", playlistName);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(CreatePlaylistActivity.this, activityClass);
        intent.putExtra("USERNAME", currentUsername);
        startActivity(intent);
    }
}