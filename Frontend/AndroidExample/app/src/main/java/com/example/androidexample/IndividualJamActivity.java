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
    private String approvalType;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton, suggestSongButton, jamSettingsButton;
    private TextView approvalTypeText;

    private boolean jamHasPlaylist = false;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individualjam);

        Toolbar toolbar = findViewById(R.id.dm_toolbar);
        setSupportActionBar(toolbar);

        currentUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");
        jamName = getIntent().getStringExtra("JAM_NAME");
        jamAdmin = getIntent().getStringExtra("JAM_ADMIN");
        approvalType = getIntent().getStringExtra("APPROVAL_TYPE");

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
        approvalTypeText = findViewById(R.id.approval_type_text);

        approvalTypeText.setText("Approval: " + approvalType);

        suggestSongButton.setEnabled(false);

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
        suggestSongButton.setOnClickListener(view -> {
            if ("Manager".equals(approvalType)) {
                showSuggestSongDialog(); // Existing logic for manager approval
            } else if ("Voting".equals(approvalType)) {
                showSuggestSongDialog(); // Same dialog, but button action will be different
            } else { // Open
                showOpenAddSongDialog();
            }
        });
        jamSettingsButton.setOnClickListener(view -> showAdminMenu());

        if (jamName != null) {
            fetchChatHistory(jamName);
        }

        createWebSocketClient();
        fetchPlaylistsAndSetupSuggestionsButton();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void fetchChatHistory(String jamName) {
        String url = HTTP_BASE_URL + "/api/jams/chatHistory/" + jamName;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        messageList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject messageJson = response.getJSONObject(i);
                            String content = messageJson.getString("content");

                            String[] parts = content.split(": ", 2);

                            if (parts.length == 2) {
                                String sender = parts[0];
                                String messageBody = parts[1];
                                try {
                                    JSONObject contentJson = new JSONObject(messageBody);
                                    String formattedMessage = formatArchivedMessage(contentJson);
                                    if (formattedMessage != null) {
                                        messageList.add(new ChatMessage("System", formattedMessage));
                                    }
                                } catch (JSONException e) {
                                    messageList.add(new ChatMessage(sender, messageBody));
                                }
                            } else if (!content.trim().isEmpty()) {
                                messageList.add(new ChatMessage("System", content));
                            }
                        }
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);

                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "JSON parsing error in chat history", e);
                    }
                },
                error -> {
                    String errorMessage = "Error loading chat history";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            errorMessage = "Error: " + error.networkResponse.statusCode + " " + responseBody;
                        } catch (Exception e) {
                            Log.e("IndividualJamActivity", "Error parsing error response", e);
                        }
                    }
                    Log.e("IndividualJamActivity", "Volley error: " + errorMessage, error);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private String formatArchivedMessage(JSONObject messageJson) {
        try {
            String type = messageJson.optString("type");
            switch (type) {
                case "vote_result":
                    String resultSong = messageJson.getString("song");
                    String result = messageJson.getString("result");
                    return result.equals("approved")
                            ? "Vote passed! '" + resultSong + "' was added to the playlist."
                            : "Vote for '" + resultSong + "' did not pass.";
                case "song_suggestion":
                    String song = messageJson.getString("song");
                    String suggester = messageJson.getString("suggester");
                    return "Suggestion sent for \"" + song + "\" by " + suggester + ".";
                case "song_vote_request":
                    String voteSong = messageJson.getString("song");
                    String suggester_vote = messageJson.getString("suggester");
                    return "Vote initiated for \"" + voteSong + "\" by " + suggester_vote + ".";
                default:

                    return null;
            }
        } catch (JSONException e) {
            Log.e("formatArchivedMessage", "Error parsing archived message", e);
            return null;
        }
    }

    private void fetchPlaylistsAndSetupSuggestionsButton() {
        String url = URL_STRING_REQ + "owner/" + jamAdmin;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    if (response.toString().contains(jamName)) {
                        jamHasPlaylist = true;
                        suggestSongButton.setEnabled(true);
                    } else {
                        jamHasPlaylist = false;
                        suggestSongButton.setEnabled(false);
                    }
                },
                error -> {
                    jamHasPlaylist = false;
                    suggestSongButton.setEnabled(false);
                    Log.e("IndividualJamActivity", "Could not fetch jam playlists", error);
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

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

                if (message.trim().contains("{")) {
                    try {
                        JSONObject messageJson = new JSONObject(message);
                        String type = messageJson.optString("type", "chat");

                        switch(type) {
                            case "song_suggestion": // Manager approval
                                if (currentUsername.equals(jamAdmin)) {
                                    String song = messageJson.getString("song");
                                    String artist = messageJson.getString("artist");
                                    String suggester = messageJson.getString("suggester");
                                    runOnUiThread(() -> showApprovalDialog(song, artist, suggester));
                                }
                                break;
                            case "song_vote_request": // Voting approval: show voting dialog to everyone
                                String song = messageJson.getString("song");
                                String artist = messageJson.getString("artist");
                                String suggester = messageJson.getString("suggester");
                                int songId = messageJson.getInt("songId");
                                runOnUiThread(() -> showVoteDialog(song, songId, artist, suggester));
                                break;
                            case "vote_result": // Final result of a vote
                                String resultSong = messageJson.getString("song");
                                String resultArtist = messageJson.getString("artist");
                                String result = messageJson.getString("result");
                                String resultMessage = result.equals("approved")
                                        ? "Vote passed! '" + resultSong + "' was added to the playlist."
                                        : "Vote for '" + resultSong + "' did not pass.";
                                if (result.equals("approved")) {
                                    addSongToPlaylist(resultSong, resultArtist);
                                }

                                runOnUiThread(() -> {
                                    messageList.add(new ChatMessage("System", resultMessage));
                                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                                    chatRecyclerView.scrollToPosition(messageList.size() - 1);
                                });
                                break;
                            default:
                                // Could be a different JSON message, log it
                                Log.w("WebSocket", "Unknown JSON message type: " + type);
                                break;
                        }
                        return; // Stop processing after handling JSON

                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "Error parsing JSON message", e);
                    }
                } else {
                    // Handle plain text chat messages
                    Log.d("WebSocket", "Going through this line");
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
            webSocketClient.send(message.getContent());
            Log.d(message.getSender(), message.getContent());
        } else {
            Toast.makeText(this, "Not connected to WebSocket", Toast.LENGTH_SHORT).show();
        }
    }


    private void showSuggestSongDialog() {
        if (!jamHasPlaylist) {
            Toast.makeText(this, "Admin has not created a playlist for this jam yet.", Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suggest a Song");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        LinearLayout searchLayout = new LinearLayout(this);
        searchLayout.setOrientation(LinearLayout.HORIZONTAL);

        final EditText searchInput = new EditText(this);
        searchInput.setHint("Enter song name");
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        searchInput.setLayoutParams(editTextParams);
        searchLayout.addView(searchInput);

        Button searchButton = new Button(this);
        searchButton.setText("Search");
        searchLayout.addView(searchButton);

        layout.addView(searchLayout);

        ScrollView scrollView = new ScrollView(this);
        TableLayout resultsTable = new TableLayout(this);
        resultsTable.setStretchAllColumns(true);

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
                searchSongsForSuggestion(query, resultsTable, dialog);
            } else {
                Toast.makeText(this, "Please enter a song to search", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void searchSongsForSuggestion(String query, TableLayout resultsTable, AlertDialog dialog) {
        String url = SONGS_SEARCH_URL + "/" + query;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        populateSuggestionSearchTable(response, resultsTable, dialog);
                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "JSON parsing error in search", e);
                        Toast.makeText(this, "Error parsing search results", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Volley Error", "Error searching songs: " + error.toString());
                    Toast.makeText(getApplicationContext(), "Song not found", Toast.LENGTH_SHORT).show();
                    int childCount = resultsTable.getChildCount();
                    if (childCount > 1) { 
                        resultsTable.removeViews(1, childCount - 1);
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }


    private void populateSuggestionSearchTable(JSONArray songs, TableLayout table, AlertDialog dialog) throws JSONException {
        int childCount = table.getChildCount();
        if (childCount > 1) {
            table.removeViews(1, childCount - 1);
        }

        for (int i = 0; i < songs.length(); i++) {
            JSONObject song = songs.getJSONObject(i);
            String songName = song.optString("songName", "N/A");
            String artist = song.optString("artist", "N/A");
            int songId = song.optInt("songId", -1);

            if (songId != -1) {
                TableRow tableRow = new TableRow(this);
                tableRow.addView(createTextViewForDialog(songName));
                tableRow.addView(createTextViewForDialog(artist));
                tableRow.addView(createSuggestButton(songId, songName, artist, dialog));
                table.addView(tableRow);
            }
        }
    }
    
    // New method for "Open" approval mode
    private void showOpenAddSongDialog() {
        if (!jamHasPlaylist) {
            Toast.makeText(this, "Admin has not created a playlist for this jam yet.", Toast.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a Song");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        LinearLayout searchLayout = new LinearLayout(this);
        searchLayout.setOrientation(LinearLayout.HORIZONTAL);

        final EditText searchInput = new EditText(this);
        searchInput.setHint("Enter song name");
        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        searchInput.setLayoutParams(editTextParams);
        searchLayout.addView(searchInput);

        Button searchButton = new Button(this);
        searchButton.setText("Search");
        searchLayout.addView(searchButton);

        layout.addView(searchLayout);

        ScrollView scrollView = new ScrollView(this);
        TableLayout resultsTable = new TableLayout(this);
        resultsTable.setStretchAllColumns(true);

        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextViewForDialog("Song"));
        headerRow.addView(createTextViewForDialog("Artist"));
        headerRow.addView(createTextViewForDialog(""));
        resultsTable.addView(headerRow);

        scrollView.addView(resultsTable);
        layout.addView(scrollView);

        AlertDialog dialog = builder.setView(layout)
                .setNegativeButton("Close", (d, which) -> d.cancel())
                .create();

        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString().trim();
            if (!query.isEmpty()) {
                searchSongsForOpenAdd(query, resultsTable, dialog);
            } else {
                Toast.makeText(this, "Please enter a song to search", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void searchSongsForOpenAdd(String query, TableLayout resultsTable, AlertDialog dialog) {
        String url = SONGS_SEARCH_URL + "/" + query;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        // Clear previous results
                        int childCount = resultsTable.getChildCount();
                        if (childCount > 1) { resultsTable.removeViews(1, childCount - 1); }

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject song = response.getJSONObject(i);
                            String songName = song.optString("songName", "N/A");
                            String artist = song.optString("artist", "N/A");

                            TableRow tableRow = new TableRow(this);
                            tableRow.addView(createTextViewForDialog(songName));
                            tableRow.addView(createTextViewForDialog(artist));

                            Button addButton = new Button(this);
                            addButton.setText("Add");
                            addButton.setOnClickListener(v -> {
                                addSongToPlaylist(songName, artist); // Add directly
                                dialog.dismiss();
                            });
                            tableRow.addView(addButton);
                            resultsTable.addView(tableRow);
                        }
                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "JSON parsing error in open search", e);
                    }
                },
                error -> Log.e("Volley Error", "Error searching songs: " + error.toString()));
        requestQueue.add(jsonArrayRequest);
    }

    private TextView createTextViewForDialog(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setWidth(10);
        return textView;
    }

    private Button createSuggestButton(final int songID, final String songName, final String artist, final AlertDialog dialog) {
        Button button = new Button(this);
        button.setText("Suggest");
        button.setOnClickListener(v -> {
            if ("Voting".equals(approvalType)) {
                startSongVote(songName, songID, artist); // Initiate a vote
            } else { // Default to Manager approval
                sendSongSuggestion(songName, artist);
            }
            dialog.dismiss();
        });
        return button;
    }
    
    // Send a suggestion for Manager-led approval
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
                Toast.makeText(this, "Song suggested to the Jam Manager!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Not connected to WebSocket", Toast.LENGTH_SHORT).show();
        }
    }

    // Start a vote for Voting-based approval
    private void startSongVote(String songName, int songID, String artist) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            JSONObject voteRequestJson = new JSONObject();
            try {
                voteRequestJson.put("type", "song_vote_request");
                voteRequestJson.put("song", songName);
                voteRequestJson.put("songId", songID);
                voteRequestJson.put("artist", artist);
                voteRequestJson.put("suggester", currentUsername);
                webSocketClient.send(voteRequestJson.toString());
                Toast.makeText(this, "Vote initiated for " + songName, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Not connected to WebSocket", Toast.LENGTH_SHORT).show();
        }
    }

    private void showVoteDialog(String song, int songId, String artist, String suggester) {
        new AlertDialog.Builder(this)
                .setTitle("Vote for a Song")
                .setMessage(suggester + " wants to add \"" + song + "\" by " + artist + ".")
                .setPositiveButton("Yes", (dialog, which) -> sendVote(song, songId, artist,"yes"))
                .setNegativeButton("No", (dialog, which) -> sendVote(song, songId, artist, "no"))
                .show();
    }

    private void sendVote(String songName, int songId, String artist, String vote) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            JSONObject voteJson = new JSONObject();
            try {
                voteJson.put("type", "song_vote");
                voteJson.put("song", songName);
                voteJson.put("artist", artist);
                voteJson.put("voter", currentUsername);
                voteJson.put("vote", vote);
                voteJson.put("songId", songId);
                webSocketClient.send(voteJson.toString());
                Toast.makeText(this, "You voted " + vote, Toast.LENGTH_SHORT).show();
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
                    Log.d("Volley Response", response.toString());
                    Toast.makeText(this, "Playlist Created", Toast.LENGTH_SHORT).show();
                    if (webSocketClient != null && webSocketClient.isOpen()) {
                        webSocketClient.send("Admin " + jamAdmin + " created a playlist for this jam");
                    }
                    fetchPlaylistsAndSetupSuggestionsButton();
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
                    addSongToPlaylist(song, artist);
                    Toast.makeText(this, song + " approved!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Reject", (dialog, which) -> {
                    if (webSocketClient != null && webSocketClient.isOpen()) {
                        webSocketClient.send("Suggestion for '" + song + "' was denied by the admin");
                    }
                })
                .show();
    }

    private void addSongToPlaylist(final String songName, final String artist) {
        String url = URL_STRING_REQ + jamAdmin + "/" + jamName + "/add";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Volley Response", "Add song: " + response);
                    Toast.makeText(getApplicationContext(), "Song added", Toast.LENGTH_SHORT).show();
                    if (webSocketClient != null && webSocketClient.isOpen()) {
                        String notification = "Manager".equals(approvalType)
                            ? "Suggestion for '" + songName + "' was approved by the admin"
                            : "'" + songName + "' was added to the playlist by " + currentUsername;
                        webSocketClient.send(notification);
                    }
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
