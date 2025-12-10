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

/**
 * Represents the Direct Messaging (DM) screen of the application.
 * This activity allows two users to exchange private messages in real-time.
 * It fetches chat history and uses WebSockets for live communication.
 */
public class DMActivity extends AppCompatActivity {

    // Base URL for HTTP requests to the backend server.
    private static final String HTTP_BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080/api";
    // WebSocket URL for real-time chat.
    private String WEB_SOCKET_URL;

    // Volley request queue for network requests.
    private RequestQueue requestQueue;
    // Username of the currently logged-in user.
    private String currentUsername;
    // Username of the friend the user is messaging.
    private String friendUsername;

    // UI elements
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;

    // Adapter and list for managing chat messages in the RecyclerView.
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    // WebSocket client for real-time messaging.
    private WebSocketClient webSocketClient;

    /**
     * Called when the activity is first created. Initializes the UI, sets up
     * the WebSocket connection, and fetches the chat history.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in onSaveInstanceState(Bundle). Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dm);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.dm_toolbar);
        setSupportActionBar(toolbar);

        // Get usernames from the intent that started this activity.
        currentUsername = getIntent().getStringExtra("LOGGED_IN_USERNAME");
        friendUsername = getIntent().getStringExtra("FRIEND_USERNAME");

        // Construct the WebSocket URL with the current user's username.
        WEB_SOCKET_URL = "ws://coms-3090-008.class.las.iastate.edu:8080/websocket/chat/" + currentUsername;

        // Initialize the Volley request queue.
        requestQueue = Volley.newRequestQueue(this);

        // Configure the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("DM with " + friendUsername);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components.
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        // Initialize the message list and adapter.
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList, currentUsername);

        // Configure the RecyclerView.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        // Set a click listener for the send button.
        sendButton.setOnClickListener(view -> sendMessage());

        // Fetch chat history if usernames are available.
        if (currentUsername != null && friendUsername != null) {
            fetchChatHistory(currentUsername, friendUsername);
        }

        // Establish the WebSocket connection.
        createWebSocketClient();
    }

    /**
     * Handles the "up" navigation action from the toolbar. Finishes the current
     * activity, returning the user to the previous screen.
     *
     * @return true if navigation was handled successfully.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Fetches the chat history between two users from the backend server.
     *
     * @param user1 The username of the first user.
     * @param user2 The username of the second user.
     */
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

    /**
     * Creates and connects the WebSocket client.
     */
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

    /**
     * Sends a message from the input field. The message is added to the UI
     * and sent to the backend via WebSocket.
     */
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

    /**
     * Sends a chat message to the backend server via WebSocket.
     *
     * @param message The ChatMessage object to send.
     */
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

    /**
     * Called when the activity is being destroyed. Closes the WebSocket connection
     * to release resources.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}