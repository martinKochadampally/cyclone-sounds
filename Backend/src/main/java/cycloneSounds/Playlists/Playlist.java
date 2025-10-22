package cycloneSounds.Playlists;

import cycloneSounds.Credentials.Credentials;
import jakarta.persistence.*;

public class Playlist {

    //Will probably use song table and song object to generate song information
    //Will resolve repetition and be easier to design playlists
    @Id
    @Column(unique = true)
    private String playlistName;
    private String users;
    private String songName;
    private String artist;
    private String genre;


    /*
     * @OneToOne creates a relation between the current entity/table(Laptop) with the entity/table defined below it(User)
     * cascade is responsible propagating all changes, even to children of the class Eg: changes made to laptop within a user object will be reflected
     * in the database (more info : https://www.baeldung.com/jpa-cascade-types)
     * @JoinColumn defines the ownership of the foreign key i.e. the user table will have a field called laptop_id
     */
    //@OneToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "username")
    //private Credentials credential;

    public Playlist(String playlistName, String users, String songName, String artist, String genre) {
        this.playlistName = playlistName;
        this.users = users;
        this.songName = songName;
        this.artist = artist;
        this.genre = genre;
    }

    public Playlist() {
    }

    // =============================== Getters and Setters for each field ================================== //
    public String getPlaylistName(){
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getUsers(){
        return users;
    }

    public void setUsers(String users){
        this.users = users;
    }

    public String getSongName(){
        return songName;
    }

    public void setSongName(String songName) { this.songName = songName;}

    public String getArtist(){
        return artist;
    }

    public void setArtist(String artist) { this.artist = artist;}

    public String getGenre(){
        return genre;
    }

    public void setGenre(String genre) { this.genre = genre;}
}
