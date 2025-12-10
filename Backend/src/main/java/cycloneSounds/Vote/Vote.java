package cycloneSounds.Vote;

import cycloneSounds.Jams.Jam;
import cycloneSounds.Songs.Song;
import jakarta.persistence.*;
import lombok.*;

@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int voteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jamName")
    @Getter
    @Setter
    private Jam jam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "songId")
    @Getter
    @Setter
    private Song song;

    @Getter
    @Setter
    private String suggester;

    @Getter @Setter
    private String voteStatus;

    public  Vote() {

    }

    public Vote(Jam jam, Song song, String suggester) {
        this.jam = jam;
        this.song = song;
        this.suggester = suggester;
        this.voteStatus = "inprogress";
    }
}
