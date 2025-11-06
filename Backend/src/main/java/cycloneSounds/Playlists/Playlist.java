package cycloneSounds.Playlists;

import cycloneSounds.Credentials.Credentials;
import cycloneSounds.Songs.Song;
import jakarta.persistence.*;

import cycloneSounds.profilePage.Profile;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Profile owner;

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

    public Profile getOwner() {
        return owner;
    }

    public void setOwner(Profile owner) {
        this.owner = owner;
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