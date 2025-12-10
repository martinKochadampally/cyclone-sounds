package cycloneSounds.AlbumReview;

public class AlbumReviewRequest {

    private String username;
    private Integer albumId;
    private int rating;
    private String reviewText;


    public AlbumReviewRequest() {
    }

    public AlbumReviewRequest(String username, Integer albumId, int rating, String reviewText) {
        this.username = username;
        this.albumId = albumId;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public int getAlbumRating() {
        return rating;
    }

    public void setAlbumRating(int rating) {
        this.rating = rating;
    }

    public String getAlbumRequestReviewText() {
        return reviewText;
    }

    public void setAlbumReviewText(String reviewText) {
        this.reviewText = reviewText;
    }
}