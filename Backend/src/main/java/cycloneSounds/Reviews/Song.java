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

    // A song doesn't have a reviewer, rating, or body. The Review does.
    // So we remove those fields from here.

    public Song() {}

    // The constructor now only needs the song's name and artist
    public Song(String songName, String artist) {
        this.songName = songName;
        this.artist = artist;
    }

    // --- Getters and Setters ---

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
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
}