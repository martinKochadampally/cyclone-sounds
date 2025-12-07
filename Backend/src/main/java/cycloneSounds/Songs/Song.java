package cycloneSounds.Songs;

import jakarta.persistence.*;

/**
 * Song table that is updated from spotify API. Songs can be manually added through a post method through artist or song name
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = {"spotifyId"})})
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int songId;

    private String songName;
    private String artist;
    private int searches;

    @Column(name = "spotifyId")
    private String spotifyId;
    
    public Song() {}

    public Song(String songName, String artist) {
        this.songName = songName;
        this.artist = artist;
        this.searches = 0;
    }

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

    public int getSearches() {
        return searches;
    }

    public void setSearches(int searches) {this.searches = searches;}

    public void incrementSearches() {
        this.searches++;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    //This is a getter to send the exact url for frontend to play the song in webPlayer.
    public String getEmbedUrl() {
        return "https://open.spotify.com/embed/track/" + this.spotifyId;
    }
}