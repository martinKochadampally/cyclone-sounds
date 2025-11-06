package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DMActivity extends AppCompatActivity {

    private static final String HTTP_BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/api";
    private String WEB_SOCKET_URL;

    private RequestQueue requestQueue;
    private String currentUsername;
    private String friendUsername;

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    private WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dm);

        Toolbar toolbar = findViewById(R.id.dm_toolbar);
        setSupportActionBar(toolbar);

        currentUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");
        friendUsername = getIntent().getStringExtra("FRIEND_USERNAME");

        WEB_SOCKET_URL = "ws://coms-3090-008.class.las.iastate.edu:8080/websocket/chat/" + currentUsername;

        requestQueue = Volley.newRequestQueue(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("DM with " + friendUsername);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, currentUsername);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(view -> sendMessage());

        if (currentUsername != null && friendUsername != null) {
            fetchChatHistory(currentUsername, friendUsername);
        }

        createWebSocketClient();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchChatHistory(String user1, String user2) {
        String url = HTTP_BASE_URL + "/chat/history/" + user1 + "/" + user2;

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
                        Log.e("DMActivity", "JSON parsing error", e);
                    }
                },
                error -> {
                    String errorMessage = "Error loading chat history";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            errorMessage = "Error: " + error.networkResponse.statusCode + " " + responseBody;
                        } catch (Exception e) {
                            Log.e("DMActivity", "Error parsing error response", e);
                        }
                    }
                    Log.e("DMActivity", "Volley error: " + errorMessage, error);
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void createWebSocketClient() {
        URI uri;
        try {
            uri = new URI(WEB_SOCKET_URL);
            Log.d("DMActivity", "Attempting to connect to: " + uri);
        } catch (URISyntaxException e) {
            Log.e("DMActivity", "Invalid WebSocket URL", e);
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("DMActivity", "WebSocket onOpen: Connected successfully!");
            }

            @Override
            public void onMessage(String message) {
                Log.d("DMActivity", "Received message: " + message);
                try {
                    JSONObject messageJson = new JSONObject(message);
                    String sender = messageJson.getString("sender");
                    String content = messageJson.getString("content");

                    runOnUiThread(() -> {
                        messageList.add(new ChatMessage(sender, content));
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    });

                } catch (JSONException e) {
                    Log.e("DMActivity", "Error parsing received message", e);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e("DMActivity", "WebSocket onClose: Connection closed by " + (remote ? "server" : "us"));
                Log.e("DMActivity", "WebSocket onClose: Code: " + code + ", Reason: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.e("DMActivity", "WebSocket onError: An error occurred", ex);
            }
        };

        Log.d("DMActivity", "Calling webSocketClient.connect()...");
        webSocketClient.connect();
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty()) {
            return;
        }

        messageInput.setText("");

        ChatMessage message = new ChatMessage(currentUsername, content);
        messageList.add(message);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        chatRecyclerView.scrollToPosition(messageList.size() - 1);

        sendMessageToBackend(message);
    }

    private void sendMessageToBackend(ChatMessage message) {
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("receiver", friendUsername);
            requestBody.put("content", message.getContent());
        } catch (JSONException e) {
            Log.e("DMActivity", "Error creating JSON for WebSocket", e);
        }

        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(requestBody.toString());
            Log.d("DMActivity", "Sent message: " + requestBody.toString());
        } else {
            Log.e("DMActivity", "WebSocket is not connected!");
            Toast.makeText(this, "Failed to send: Not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}