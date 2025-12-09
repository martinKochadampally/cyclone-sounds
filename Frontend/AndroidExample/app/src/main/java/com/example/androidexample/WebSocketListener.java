package com.example.androidexample;

import org.java_websocket.handshake.ServerHandshake;

/**
 * This interface defines a set of callback methods for handling WebSocket events.
 * Classes that need to react to WebSocket lifecycle events (open, message, close, error)
 * should implement this interface and register themselves with a WebSocketManager.
 */
public interface WebSocketListener {

    /**
     * Called when the WebSocket connection has been established and is ready for use.
     *
     * @param handshakedata Information about the server handshake, confirming the connection details.
     */
    void onWebSocketOpen(ServerHandshake handshakedata);

    /**
     * Called when a new message is received from the WebSocket server.
     *
     * @param message The message received from the server as a String.
     */
    void onWebSocketMessage(String message);

    /**
     * Called when the WebSocket connection is closed.
     *
     * @param code   The status code indicating the reason for closure (e.g., 1000 for normal closure).
     * @param reason A human-readable explanation for why the connection was closed.
     * @param remote A boolean indicating whether the closure was initiated by the remote party (server) or locally.
     */
    void onWebSocketClose(int code, String reason, boolean remote);

    /**
     * Called when a WebSocket error occurs.
     *
     * @param ex The exception that occurred, providing details about the error.
     */
    void onWebSocketError(Exception ex);
}
