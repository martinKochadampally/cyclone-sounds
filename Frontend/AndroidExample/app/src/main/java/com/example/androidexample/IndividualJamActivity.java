package com.example.androidexample;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class IndividualJamActivity extends AppCompatActivity {

    private static final String HTTP_BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";
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


        WEB_SOCKET_URL = "ws://coms-3090-008.class.las.iastate.edu:8080/websocket/jam/" + jamName + "/"+ currentUsername;

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

        if (jamName != null) {
            fetchChatHistory(jamName);
        }

        createWebSocketClient();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchChatHistory(String jamName) {
        String url = HTTP_BASE_URL + "/api/jam/" + jamName + "/history/";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        messageList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject messageJson = response.getJSONObject(i);
                            String sender = messageJson.getString("sender");
                            String content = messageJson.getString("content");

                            messageList.add(new ChatMessage(sender, content));
                        }
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);

                    } catch (JSONException e) {
                        Log.e("IndividualJamActivity", "JSON parsing error", e);
                    }
                },
                error -> {
                    Toast.makeText(this, "Error loading chat history", Toast.LENGTH_LONG).show();
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
                try {
                    JSONObject messageJson = new JSONObject(message);
                    String type = messageJson.optString("type", "chat");

                    if ("song_suggestion".equals(type)) {
                        if (currentUsername.equals(jamAdmin)) {
                            String song = messageJson.getString("song");
                            String suggester = messageJson.getString("suggester");
                            runOnUiThread(() -> {
                                showApprovalDialog(song, suggester);
                            });
                        }
                    } else {
                        String sender = messageJson.getString("sender");
                        String content = messageJson.getString("content");

                        runOnUiThread(() -> {
                            messageList.add(new ChatMessage(sender, content));
                            chatAdapter.notifyItemInserted(messageList.size() - 1);
                            chatRecyclerView.scrollToPosition(messageList.size() - 1);
                        });
                    }

                } catch (JSONException e) {
                    Log.e("IndividualJamActivity", "Error parsing received message", e);
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
            JSONObject messageJson = new JSONObject();
            try {
                messageJson.put("type", "chat");
                messageJson.put("sender", message.getSender());
                messageJson.put("content", message.getContent());
                messageJson.put("receiver", jamName);
                webSocketClient.send(messageJson.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Not connected to WebSocket", Toast.LENGTH_SHORT).show();
        }
    }


    private void showSuggestSongDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Suggest a Song");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Suggest", (dialog, which) -> {
            String songName = input.getText().toString();
            if (!songName.isEmpty()) {
                sendSongSuggestion(songName);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void sendSongSuggestion(String songName) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            JSONObject suggestionJson = new JSONObject();
            try {
                suggestionJson.put("type", "song_suggestion");
                suggestionJson.put("song", songName);
                suggestionJson.put("suggester", currentUsername);
                suggestionJson.put("receiver", jamAdmin);
                webSocketClient.send(suggestionJson.toString());
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
                createJamPlaylist();
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void createJamPlaylist() {
        String url = HTTP_BASE_URL + "/playlists";

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("name", jamName);
            requestBody.put("owner", currentUsername);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody,
                response -> {
                    Toast.makeText(this, "Playlist '" + jamName + "' created successfully.", Toast.LENGTH_LONG).show();
                },
                error -> {
                    Toast.makeText(this, "Failed to create playlist.", Toast.LENGTH_LONG).show();
                }
        );
        requestQueue.add(jsonObjectRequest);
    }

    private void showApprovalDialog(String song, String suggester) {
        new AlertDialog.Builder(this)
                .setTitle("Song Suggestion")
                .setMessage(suggester + " suggested adding \"" + song + "\" to the playlist. Approve?")
                .setPositiveButton("Approve", (dialog, which) -> {
                    // TODO: Add song to the playlist
                    Toast.makeText(this, song + " approved!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Reject", null)
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
