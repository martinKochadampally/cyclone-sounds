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
    //private String genre;

    public Song(){}

    public Song(String songName, String artist, String reviewer) {
        this.songName = songName;
        this.artist = artist;
        this.reviewer = reviewer;
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
}
