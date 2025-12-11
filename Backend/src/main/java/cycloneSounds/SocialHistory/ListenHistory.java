package cycloneSounds.SocialHistory;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import cycloneSounds.Credentials.Credentials;

/**
 * ListenHistory represents one record of a user's listening history for a speicific song.
 * Maps to the listen_history table and acts like a time log. It links the user using credentials
 * to a songId and records time of listen.
 *
 * @author  Mark
 */
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

    /**
     * Creates new listen history record
     * @param credentials
     * @param songId
     */
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