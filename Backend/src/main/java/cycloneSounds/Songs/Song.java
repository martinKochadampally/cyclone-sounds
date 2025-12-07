package cycloneSounds.Songs;

import cycloneSounds.Albums.Album;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cycloneSounds.Albums.Album;

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

    @ManyToOne
    @JoinColumn(name = "album_id")
    @JsonIgnore
    private Album album;


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

    public Album getAlbum() {
        return album;
    }
    public void setAlbum(Album album) {
        this.album = album;
    }

    //URL for frontend to use when building embedded spotify player
    public String getEmbedUrl() {
        return "https://open.spotify.com/embed/track/" + this.spotifyId;
    }
}