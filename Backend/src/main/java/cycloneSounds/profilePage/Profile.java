package cycloneSounds.profilePage;

import jakarta.persistence.*;

import cycloneSounds.Credentials.Credentials;


/**
 * 
 * @author Mark Seward
 * 
 */
@Entity
public class Profile {

     /* 
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     */
    @Id
    @Column(unique = true)
    private String username;
    private String name;
    private String favSong;
    private String favArtist;
    private String favGenre;
    private String biography;

    /*
     * @OneToOne creates a relation between the current entity/table(Laptop) with the entity/table defined below it(User)
     * cascade is responsible propagating all changes, even to children of the class Eg: changes made to laptop within a user object will be reflected
     * in the database (more info : https://www.baeldung.com/jpa-cascade-types)
     * @JoinColumn defines the ownership of the foreign key i.e. the user table will have a field called laptop_id
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username")
    private Credentials credential;

    public Profile(String username, String name, String favSong, String favArtist, String favGenre, String biography) {
        this.username = username;
        this.name = name;
        this.favSong = favSong;
        this.favArtist = favArtist;
        this.favGenre = favGenre;
        this.biography = biography;
    }

    public Profile() {
    }

    // =============================== Getters and Setters for each field ================================== //
    public String getUsername(){
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getFavSong(){
        return favSong;
    }

    public void setFavSong(String favSong) { this.favSong = favSong;}

    public String getFavArtist(){
        return favArtist;
    }

    public void setFavArtist(String favArtist) { this.favArtist = favArtist;}

    public String getFavGenre(){
        return favGenre;
    }

    public void setFavGenre(String favGenre) { this.favGenre = favGenre;}

    public String getBiography(){
        return biography;
    }

    public void setBiography(String biography) { this.biography = biography;}


}
