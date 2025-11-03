package cycloneSounds.chat;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "DMmessages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "sender")
    private String senderUsername;
    @Column(name = "receiver")
    private String receiverUsername;
    @Column(name = "messages")
    private String content;

    @CreationTimestamp
    private Instant timestamp;

    public ChatMessage(String senderUsername, String receiverUsername, String content) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.content = content;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }
}