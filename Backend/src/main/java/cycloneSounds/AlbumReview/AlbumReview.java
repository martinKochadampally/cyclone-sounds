package cycloneSounds.AlbumReview;

import cycloneSounds.Albums.Album;
import jakarta.persistence.*;

@Entity
@Table(name = "album_review")
public class AlbumReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String reviewer;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    private float rating;
    private String reviewText;
    private String bestSong;
    private String worstSong;

    public AlbumReview() {}

    public AlbumReview(String reviewer, Album album, float rating, String reviewText, String bestSong, String worstSong) {
        this.reviewer = reviewer;
        this.album = album;
        this.rating = rating;
        this.reviewText = reviewText;
        this.bestSong = bestSong;
        this.worstSong = worstSong;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getReviewer() { return reviewer; }
    public void setReviewer(String reviewer) { this.reviewer = reviewer; }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }

    public float getAlbumReviewRating() { return rating; }
    public void setAlbumRating(float rating) { this.rating = rating; }

    public String getAlbumReviewReviewText() { return reviewText; }
    public void setAlbumReviewText(String reviewText) { this.reviewText = reviewText; }

    public String getBestSong() { return bestSong; }
    public void setBestSong(String bestSong) { this.bestSong = bestSong; }

    public String getWorstSong() { return worstSong; }
    public void setWorstSong(String worstSong) { this.worstSong = worstSong; }
}