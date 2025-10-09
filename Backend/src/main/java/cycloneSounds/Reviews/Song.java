package cycloneSounds.Reviews;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"songName", "artist"})})
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int songId;

    private String songName;
    private String artist;
    private String reviewer;
    private double rating;
    private String body;
    //private String genre;

    public Song(){}

    public Song(String songName, String artist, String reviewer, double rating, String body) {
        this.songName = songName;
        this.artist = artist;
        this.reviewer = reviewer;
        this.rating = rating;
        this.body = body;
    }

    public int getSongId(){
        return songId;
    }

    public void setSongId(int songId){
        this.songId = songId;
    }

    public String getSongName(){
        return songName;
    }

    public void setSongName(String songName){
        this.songName = songName;
    }

    public String getArtist(){
        return artist;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public String getReviewer() { return reviewer;}

    public void setReviewer(String reviewer){ this.reviewer = reviewer;}

    public double getRating(){ return rating;}

    public void setRating(int rating){ this.rating = rating;}

    public String getBody(){ return body;}

    public void setBody(String body){ this.body = body;}
}
