package cycloneSounds.Reviews;

import jakarta.persistence.*;


public class CreateReviewFile {

    private String reviewer;
    private String songName;
    private String artist;
    private double rating;
    private String body;

    public CreateReviewFile() {
    }

    public CreateReviewFile(String reviewer, String songName, String artist, String body, double rating) {
        this.reviewer = reviewer;
        this.rating = rating;
        this.body = body;
        this.songName = songName;
        this.artist = artist;

    }

    public String getReviewer() {
        return reviewer;
    }

    public void setReviewer(String reviewer) {
        this.reviewer = reviewer;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

}