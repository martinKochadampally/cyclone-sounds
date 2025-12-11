package com.example.androidexample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpotifyPlayerActivity extends AppCompatActivity {

    private Button closeButton;
    private Button reviewButton;
    private String currentUsername;
    private String songName;
    private String artistName;
    private ListView historyListView;
    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";

    // Inner class to match the JSON structure of the history response
    public static class HistoryResponse {
        String username;

        @SerializedName(value = "listenedAt", alternate = {"time", "timeAgo"})
        String listenedAt;

        @Override
        public String toString() {
            return "HistoryResponse{" +
                    "username='" + username + "'" +
                    ", listenedAt='" + listenedAt + "'" +
                    '}';
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_player);

        WebView webView = findViewById(R.id.spotify_web_view);
        historyListView = findViewById(R.id.history_list_view);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Spotify's player requires JavaScript
        webView.setWebViewClient(new WebViewClient()); // Ensures links open within the WebView

        String embedUrl = getIntent().getStringExtra("EMBED_URL");
        currentUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");
        songName = getIntent().getStringExtra("SONG_NAME");
        artistName = getIntent().getStringExtra("ARTIST_NAME");
        int songId = getIntent().getIntExtra("SONG_ID", -1);

        changeSongPlayInfo(songId);


        closeButton = findViewById(R.id.close_button);
        reviewButton = findViewById(R.id.review_button);

        closeButton.setOnClickListener(v -> finish());

        reviewButton.setOnClickListener(v -> {
            Intent intent = new Intent(SpotifyPlayerActivity.this, CreateReviewActivity.class);
            intent.putExtra("LOGGED_IN_USERNAME", currentUsername);
            intent.putExtra("SONG_NAME", songName);
            intent.putExtra("ARTIST_NAME", artistName);
            startActivity(intent);
        });

        if (embedUrl != null && !embedUrl.isEmpty()) {
            webView.loadUrl(embedUrl);
        } else {
            finish();
        }

         getSongHistory(songId);
    }

    private void changeSongPlayInfo(int songId) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                BASE_URL + "/history/record",
                response -> Log.d("Volley Response", response),
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Post Error", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", currentUsername);
                params.put("songId", String.valueOf(songId));
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getSongHistory(int songId) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + "/history/song/" + songId,
                response -> {
                    Log.d("Volley Response", response);
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<HistoryResponse>>() {}.getType();
                    List<HistoryResponse> historyList = gson.fromJson(response, listType);

                    HistoryAdapter adapter = new HistoryAdapter(this, historyList);
                    historyListView.setAdapter(adapter);

                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    Toast.makeText(getApplicationContext(), "Get Error", Toast.LENGTH_LONG).show();
                }
        );
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    // Custom adapter for the history list
    private class HistoryAdapter extends ArrayAdapter<HistoryResponse> {
        private final Context context;
        private final List<HistoryResponse> values;

        public HistoryAdapter(Context context, List<HistoryResponse> values) {
            super(context, R.layout.list_item_history, values);
            this.context = context;
            this.values = values;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_history, parent, false);

            TextView usernameTextView = rowView.findViewById(R.id.username_text_view);
            TextView listenedAtTextView = rowView.findViewById(R.id.listened_at_text_view);

            HistoryResponse item = values.get(position);
            usernameTextView.setText(item.username);
            listenedAtTextView.setText(item.listenedAt);

            return rowView;
        }
    }
}
