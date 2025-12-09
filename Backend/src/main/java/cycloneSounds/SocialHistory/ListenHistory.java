package cycloneSounds.SocialHistory;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import cycloneSounds.Credentials.Credentials;

@Entity
@Table(name = "listen_history")
public class ListenHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "username_id", nullable = false)
    private Credentials credentials;


    @Column(name = "song_id", nullable = false)
    private String songId;


    @Column(name = "listened_at", nullable = false)
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