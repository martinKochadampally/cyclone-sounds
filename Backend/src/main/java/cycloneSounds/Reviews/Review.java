package cycloneSounds.Reviews;

import jakarta.persistence.*;

/**
 *
 * @author mark seward
 */
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String reviewer;

    @ManyToOne
    @JoinColumn(name = "song_ID", nullable = false)
    private Song song;

    private double rating;
    private String description;
    private int upVotes = 0;
    private int downVotes = 0;

    public Review () {}

    public Review (String reviewer, double rating, String description, String songName, String artist) {
        this.reviewer = reviewer;
        this.rating = rating;
        this.description = description;

        this.upVotes = 0;
        this.downVotes = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void incrementUpVotes() {
        this.upVotes += 1;
    }

    public void decrementUpVotes() {
        this.upVotes -= 1;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void incrementDownVotes() {
        this.downVotes += 1;
    }

    public void decrementDownVotes() {
        this.downVotes -= 1;
    }
}
