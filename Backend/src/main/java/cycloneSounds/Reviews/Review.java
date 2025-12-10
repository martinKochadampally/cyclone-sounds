package cycloneSounds.Reviews;

import cycloneSounds.Songs.Song;
import cycloneSounds.Albums.Album;
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
    @JoinColumn(name = "song_id", nullable = true)
    private Song song;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = true)
    private Album album;

    private double rating;
    private String body;

    private int upVotes = 0;
    private int downVotes = 0;

    public Review () {}


    public Review (String reviewer, double rating, String body) {
        this.reviewer = reviewer;
        this.rating = rating;
        this.body = body;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getUpVotes() {
        return upVotes;
    }

    public void incrementUpVotes() {
        this.upVotes++;
    }

    public int getDownVotes() {
        return downVotes;
    }

    public void incrementDownVotes() {
        this.downVotes++;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
