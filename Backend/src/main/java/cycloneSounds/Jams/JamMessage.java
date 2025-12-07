package cycloneSounds.Jams;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.Data;

@Entity
@Table(name = "jamMessages")
@Data
public class JamMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "jam_name", nullable = false)
    @JsonIgnore
    private Jam jam;

    @Lob
    @Column(nullable = false)
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent", nullable = false)
    private Date sent;

    // Optional: message type for possible message/command distinction
    @Column(nullable = false)
    private String messageType = "CHAT";  // default "CHAT", can be "PLAYLIST_CMD", "SYSTEM", etc.

    public JamMessage() {
        this.sent = new Date();
    }

    public JamMessage(String userName, Jam jam, String content) {
        this.userName = userName;
        this.jam = jam;
        this.content = content;
        this.sent = new Date();
    }

    // Getters and setters (can be omitted if using Lombok's @Data)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Jam getJam() {
        return jam;
    }

    public void setJam(Jam jam) {
        this.jam = jam;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSent() {
        return sent;
    }

    public void setSent(Date sent) {
        this.sent = sent;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
