package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndividualJamActivity extends AppCompatActivity {

    private static final String HTTP_BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";
    private static final String URL_STRING_REQ = "http://coms-3090-008.class.las.iastate.edu:8080/api/playlists/";
    private static final String SONGS_SEARCH_URL = "http://coms-3090-008.class.las.iastate.edu:8080/search/songs";
    private String WEB_SOCKET_URL;
    private RequestQueue requestQueue;
    private String currentUsername;
    private String jamName;
    private String jamAdmin;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton, suggestSongButton, jamSettingsButton;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individualjam);

        Toolbar toolbar = findViewById(R.id.dm_toolbar);
        setSupportActionBar(toolbar);

        currentUsername = getIntent().getStringExtra("USERNAME");
        jamName = getIntent().getStringExtra("JAM_NAME");
        jamAdmin = getIntent().getStringExtra("JAM_ADMIN");

        Log.d("IndividualJamActivity", "Jam Admin: " + jamAdmin);


        WEB_SOCKET_URL = "ws://coms-3090-008.class.las.iastate.edu:8080/websocket/jams/" + jamName + "/"+ currentUsername;

        requestQueue = Volley.newRequestQueue(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(jamName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_btn);
        suggestSongButton = findViewById(R.id.suggest_song_btn);
        jamSettingsButton = findViewById(R.id.jam_settings_btn);

        if (currentUsername != null && currentUsername.equals(jamAdmin)) {
            jamSettingsButton.setVisibility(View.VISIBLE);
        }

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, currentUsername);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(view -> sendMessage());
        suggestSongButton.setOnClickListener(view -> showSuggestSongDialog());
        jamSettingsButton.setOnClickListener(view -> showAdminMenu());
//
//        if (jamName != null) {
//            fetchChatHistory(jamName);
//        }

        createWebSocketClient();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

//    private void fetchChatHistory(String jamName) {
//        String url = HTTP_BASE_URL + "/api/jam/" + jamName + "/history/";
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
//                response -> {
//                    try {
//                        messageList.clear();
//                        for (int i = 0; i < response.length(); i++) {
//                            JSONObject messageJson = response.getJSONObject(i);
//                            String sender = messageJson.getString("sender");
//                            String content = messageJson.getString("content");
//
//                            messageList.add(new ChatMessage(sender, content));
//                        }
//                        chatAdapter.notifyDataSetChanged();
//                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
//
//                    } catch (JSONException e) {
//                        Log.e("IndividualJamActivity", "JSON parsing error", e);
//                    }
//                },
//                error -> {
//                    Toast.makeText(this, "Error loading chat history", Toast.LENGTH_LONG).show();
//                }
//        );
//
//        requestQueue.add(jsonArrayRequest);
//    }

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI(WEB_SOCKET_URL);
        } catch (URISyntaxException e) {
            Log.e("IndividualJamActivity", "Invalid WebSocket URL", e);
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("IndividualJamActivity", "WebSocket Connected");
            }

            @Override
            public void onMessage(String message) {
                Log.d("IndividualJamActivity", "Received message: " + message);

                // Check if message is JSON
                if (message.trim().startsWith("{")) {
                    try {
                        JSONObject messageJson = new JSONObject(message);
                        String type = messageJson.optString("type", "chat");

                        if ("song_suggestion".equals(type)) {
                            if (currentUsername.equals(jamAdmin)) {
                                String song = messageJson.getString("song");
                                String artist = messageJson.getString("artist");
                                String suggester = messageJson.getString("suggester");
                                runOnUiThread(() -> showApprovalDialog(song, artist, suggester));
                            }
                            return;
                        }
                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "Error parsing JSON message", e);
                    }
                }

                // Handle plain text message (format: "username: content")
                runOnUiThread(() -> {
                    String[] parts = message.split(": ", 2);
                    if (parts.length == 2) {
                        messageList.add(new ChatMessage(parts[0], parts[1]));
                    } else {
                        messageList.add(new ChatMessage("System", message));
                    }
                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("WebSocket", "Closed");
            }

            @Override
            public void onError(Exception ex) {
                Log.e("WebSocket", "Error", ex);
            }
        };
        webSocketClient.connect();
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }
        messageInput.setText("");
        sendMessageToBackend(new ChatMessage(currentUsername, content), jamName);
    }

    private void sendMessageToBackend(ChatMessage message, String jamName) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            // Send plain text for chat messages
            webSocketClient.send(message.getContent());
            Log.d(message.getSender(), message.getContent());
        } else {
            Toast.makeText(this, "Not connected to WebSocket", Toast.LENGTH_SHORT).show();
        }
    }


    private void showSuggestSongDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suggest a Song");

        // Create a layout for the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // Search input and button in a horizontal layout
        LinearLayout searchLayout = new LinearLayout(this);
        searchLayout.setOrientation(LinearLayout.HORIZONTAL);

        final EditText searchInput = new EditText(this);
        searchInput.setHint("Enter song name");
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        searchInput.setLayoutParams(editTextParams);
        searchLayout.addView(searchInput);

        Button searchButton = new Button(this);
        searchButton.setText("Search");
        searchLayout.addView(searchButton);

        layout.addView(searchLayout);

        // Results table in a ScrollView
        ScrollView scrollView = new ScrollView(this);
        TableLayout resultsTable = new TableLayout(this);
        resultsTable.setStretchAllColumns(true);

        // Add a header row to the table
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextViewForDialog("Song"));
        headerRow.addView(createTextViewForDialog("Artist"));
        headerRow.addView(createTextViewForDialog("")); // for button
        resultsTable.addView(headerRow);

        scrollView.addView(resultsTable);
        layout.addView(scrollView);

        AlertDialog dialog = builder.setView(layout)
                .setNegativeButton("Close", (d, which) -> d.cancel())
                .create();

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                searchSongsForSuggestion(query, resultsTable);
            } else {
                Toast.makeText(this, "Please enter a song to search", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void searchSongsForSuggestion(String query, TableLayout resultsTable) {
        String url = SONGS_SEARCH_URL + "/" + query;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        populateSuggestionSearchTable(response, resultsTable);
                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "JSON parsing error in search", e);
                        Toast.makeText(this, "Error parsing search results", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", "Error searching songs: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Song not found", Toast.LENGTH_SHORT).show();
                    // Clear previous results
                    int childCount = resultsTable.getChildCount();
                    if (childCount > 1) { // Keep header
                        resultsTable.removeViews(1, childCount - 1);
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void populateSuggestionSearchTable(JSONArray songs, TableLayout table) throws JSONException {
        // Clear previous results, keeping the header
        int childCount = table.getChildCount();
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }

        for (int i = 0; i < songs.length(); i++) {
            JSONObject song = songs.getJSONObject(i);
            String songName = song.optString("songName", "N/A");
            String artist = song.optString("artist", "N/A");

            TableRow tableRow = new TableRow(this);
            tableRow.addView(createTextViewForDialog(songName));
            tableRow.addView(createTextViewForDialog(artist));
            tableRow.addView(createSuggestButton(songName, artist));

            table.addView(tableRow);
        }
    }

    private TextView createTextViewForDialog(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setWidth(10);
        return textView;
    }

    private Button createSuggestButton(final String songName, final String artist) {
        Button button = new Button(this);
        button.setText("Suggest");
        button.setOnClickListener(v -> {
            sendSongSuggestion(songName, artist);
        });
        return button;
    }

    private void sendSongSuggestion(String songName, String artist) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            JSONObject suggestionJson = new JSONObject();
            try {
                suggestionJson.put("type", "song_suggestion");
                suggestionJson.put("song", songName);
                suggestionJson.put("artist", artist);
                suggestionJson.put("suggester", currentUsername);
                suggestionJson.put("receiver", jamAdmin);
                webSocketClient.send(suggestionJson.toString());
                Log.d("Song Suggestion", suggestionJson.toString());
                Toast.makeText(this, "Song suggested!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Not connected to WebSocket", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAdminMenu() {
        final CharSequence[] options = {"Create Jam Playlist", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Jam Settings");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Create Jam Playlist")) {
                createPlaylistRequest(currentUsername, jamName);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void createPlaylistRequest(final String user, final String playlistName) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL_STRING_REQ + "create",
                response -> {
                    Log.d("Volley Response", response);
                    Toast.makeText(this, "Playlist Created", Toast.LENGTH_SHORT).show();
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
                params.put("username", user);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void showApprovalDialog(String song, String artist, String suggester) {
        new AlertDialog.Builder(this)
                .setTitle("Song Suggestion")
                .setMessage(suggester + " suggested adding \"" + song + "\" to the playlist. Approve?")
                .setPositiveButton("Approve", (dialog, which) -> {
                    // TODO: Add song to the playlist
                    addSongToPlaylist(song, artist);
                    Toast.makeText(this, song + " approved!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Reject", null)
                .show();
    }

    private void addSongToPlaylist(final String songName, final String artist) {
        String url = URL_STRING_REQ + currentUsername + "/" + jamName + "/add";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Volley Response", "Add song: " + response);
                    Toast.makeText(getApplicationContext(), "Song added", Toast.LENGTH_SHORT).show();
                    // Refresh the playlist to show the newly added song
                },
                error -> {
                    Log.e("Volley Error", "Error adding song: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Failed to add song", Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("songName", songName);
                params.put("artist", artist);

                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}