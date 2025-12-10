package cycloneSounds.Vote;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
public class UserVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voteId", nullable = false)
    @Getter
    @Setter
    private Vote vote;

    @Column(nullable = false)
    @Getter
    @Setter
    private String username;

    // "yes" or "no"
    @Column(nullable = false)
    @Getter
    @Setter
    private String choice;

    public UserVote() {}

    public UserVote(Vote vote, String username, String choice) {
        this.vote = vote;
        this.username = username;
        this.choice = choice;
    }
}
