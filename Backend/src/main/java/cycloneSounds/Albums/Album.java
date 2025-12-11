package cycloneSounds.Albums;

import cycloneSounds.Songs.Song;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * This class represents an album within cycloneSOunds. This class is the container for song objects
 * OnetoMany relationship --> An album contains many songs
 * @author Mark Seard
 */
@Entity
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int albumId;

    private String title;
    private String artist;

    @OneToMany(mappedBy = "album", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Song> songs = new ArrayList<>();

    private String spotifyId;

    /**
     * runs before the Album entity is deleted. Iterates through the album songs and nulls the relationship
     * to the album which avoids key constraint problems or cascade deletes.
     */
    @PreRemove
    private void preRemove() {
        if (songs != null) {
            for (Song song : songs) {
                song.setAlbum(null);
            }
        }
    }

    public Album() {}

    public Album(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }
}