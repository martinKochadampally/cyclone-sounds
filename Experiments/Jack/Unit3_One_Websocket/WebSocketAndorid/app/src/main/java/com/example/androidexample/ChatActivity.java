package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.handshake.ServerHandshake;

import java.util.Iterator;


public class ChatActivity extends AppCompatActivity implements WebSocketListener{

    private Button sendBtn;
    private Button disconnectBtn;
    private EditText msgEtx;
    private TextView msgTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        sendBtn = (Button) findViewById(R.id.sendBtn);
        disconnectBtn = (Button) findViewById(R.id.disconnectBtn);
        msgEtx = (EditText) findViewById(R.id.msgEdt);
        msgTv = (TextView) findViewById(R.id.tx1);

        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);

        sendBtn.setOnClickListener(v -> {
            try {
                WebSocketManager.getInstance().sendMessage(msgEtx.getText().toString());
                msgEtx.setText("");
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage().toString());
            }
        });

        disconnectBtn.setOnClickListener(v -> {
            try {
                WebSocketManager.getInstance().disconnectWebSocket();
            } catch (Exception e) {
                Log.d("ExceptionDisconnect:", e.getMessage().toString());
            }
        });
    }

    @Override
    public void onWebSocketMessage(String message) {
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "\n"+message);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            String s = msgTv.getText().toString();
            msgTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}


    @Override
    public void onWebSocketError(Exception ex) {}
}