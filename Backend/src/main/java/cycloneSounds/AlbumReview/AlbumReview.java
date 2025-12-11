package cycloneSounds.AlbumReview;

import cycloneSounds.Albums.Album;
import jakarta.persistence.*;

/**
 * This java class is used to keep track of reviews created by users on albums.
 * It is mapped to the album_review table in the database and is used as a data object
 * to store the information for the variables below
 */
@Entity
@Table(name = "album_review")
public class AlbumReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String albumReviewer;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    private int rating;
    private String reviewText;
    private String bestSong;
    private String worstSong;

    public AlbumReview() {}

    public AlbumReview(String albumReviewer, Album album, int rating, String reviewText, String bestSong, String worstSong) {
        this.albumReviewer = albumReviewer;
        this.album = album;
        this.rating = rating;
        this.reviewText = reviewText;
        this.bestSong = bestSong;
        this.worstSong = worstSong;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAlbumReviewer() { return albumReviewer; }
    public void setAlbumReviewer(String albumReviewer) { this.albumReviewer = albumReviewer; }

    public Album getAlbum() { return album; }
    public void setReviewingAnAlbum(Album album) { this.album = album; }

    public int getAlbumReviewRating() { return rating; }
    public void setAlbumRating(int rating) { this.rating = rating; }

    public String getAlbumReviewReviewText() { return reviewText; }
    public void setAlbumReviewText(String reviewText) { this.reviewText = reviewText; }

    public String getBestSong() { return bestSong; }
    public void setBestSong(String bestSong) { this.bestSong = bestSong; }

    public String getWorstSong() { return worstSong; }
    public void setWorstSong(String worstSong) { this.worstSong = worstSong; }
}