package cycloneSounds.SocialHistory;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import cycloneSounds.Credentials.Credentials;

@Entity
@Table(name = "listenHistory")
public class ListenHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "usernameId", nullable = false)
    private Credentials credentials;


    @Column(name = "songId", nullable = false)
    private String songId;


    @Column(name = "listenedAt", nullable = false)
    private LocalDateTime listenedAt;

    public ListenHistory() {
    }

    public ListenHistory(Credentials credentials, String songId) {
        this.credentials = credentials;
        this.songId = songId;
        this.listenedAt = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }
    public Credentials getCredentials() {
        return credentials;
    }
    public String getSongId() {
        return songId;
    }
    public LocalDateTime getListenedAt() {
        return listenedAt;
    }
}