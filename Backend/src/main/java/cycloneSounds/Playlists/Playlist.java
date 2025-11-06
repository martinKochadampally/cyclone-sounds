package cycloneSounds.Playlists;

import cycloneSounds.Credentials.Credentials;
import cycloneSounds.Songs.Song;
import jakarta.persistence.*;

import cycloneSounds.profilePage.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/*
 * @OneToOne creates a relation between the current entity/table(Laptop) with the entity/table defined below it(User)
 * cascade is responsible propagating all changes, even to children of the class Eg: changes made to laptop within a user object will be reflected
 * in the database (more info : https://www.baeldung.com/jpa-cascade-types)
 * @JoinColumn defines the ownership of the foreign key i.e. the user table will have a field called laptop_id
 */
@Entity
@Table(name = "playlist") // Added this to ensure the table name is correct
public class Playlist {

    @Id
    @Column(nullable = false, unique = true)
    private String playlistName;

    @JoinColumn(name = "owner_id")
    private String username;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "playlist_songs", joinColumns = @JoinColumn(name = "playlist_name"), inverseJoinColumns = @JoinColumn(name = "song_id"))
    private Set<Song> songs = new HashSet<>();

    @Getter
    private int searches;

    public Playlist() {
    }

    public Playlist(String playlistName, String username) {
        this.playlistName = playlistName;
        this.username = username;
        this.searches = 0;
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

    public void incrementSearches() {
        this.searches++;
    }
}