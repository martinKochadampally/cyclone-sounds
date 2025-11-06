package cycloneSounds.chat;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * This class creates a DM table that tracks all the messages sent through the DM
 */
@Entity
@Table(name = "DMmessages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sender")
    private String sender;
    @Column(name = "receiver")
    private String receiver;
    @Column(name = "messages")
    private String content;

    @CreationTimestamp
    private Instant timestamp;


    public ChatMessage(){

    }
    public ChatMessage(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }
}