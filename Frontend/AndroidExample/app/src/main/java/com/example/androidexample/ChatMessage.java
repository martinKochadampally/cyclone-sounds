package com.example.androidexample;

/**
 * Represents a single chat message within the application.
 * This class holds the information about the sender and the content of the message.
 */
public class ChatMessage {

    // The username of the message sender.
    private String sender;
    // The text content of the message.
    private String content;

    /**
     * Constructs a new ChatMessage object.
     *
     * @param sender The username of the person who sent the message.
     * @param content The content of the message.
     */
    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    /**
     * Returns the sender of the message.
     *
     * @return The sender's username.
     */
    public String getSender() {
        return sender;
    }

    /**
     * Returns the content of the message.
     *
     * @return The message content.
     */
    public String getContent() {
        return content;
    }
}
