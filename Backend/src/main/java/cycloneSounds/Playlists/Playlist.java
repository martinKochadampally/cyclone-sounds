package cycloneSounds.Playlists;

import cycloneSounds.Songs.Song;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Playlist class that makes the table in my SQL. Has owner and allows for a many to many relationship.
 * THis relationship allows there to many playlists and the same song to be in many playlists.
 */
@Entity
@Table(name = "playlist")
public class Playlist {

    @Id
    @Column(nullable = false, unique = true)
    private String playlistName;

    @JoinColumn(name = "owner_id")
    private String username;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "playlist_songs", joinColumns = @JoinColumn(name = "playlist_name"), inverseJoinColumns = @JoinColumn(name = "song_id"))
    private Set<Song> songs = new HashSet<>();

    public Playlist() {
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }

    public void removeSong(Song song) {
        this.songs.remove(song);
    }
}