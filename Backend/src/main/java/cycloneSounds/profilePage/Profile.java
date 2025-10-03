package cycloneSounds.profilePage;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;


/**
 * 
 * @author Vivek Bengre
 * 
 */ 

@Entity
public class Profile {

     /* 
     * The annotation @ID marks the field below as the primary key for the table created by springboot
     */
    @Id
    @Column(unique = true)
    private String email;

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
    //@OneToOne(cascade = CascadeType.ALL)
    //@JoinColumn(name = "laptop_id")
    //private Laptop laptop;

    public Profile(String name, String email, String favSong, String favArtist, String favGenre, String biography) {
        this.name = name;
        this.email = email;
        this.favSong = favSong;
        this.favArtist = favArtist;
        this.favGenre = favGenre;
        this.biography = biography;
    }

    public Profile() {
    }

    // =============================== Getters and Setters for each field ================================== //

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }


    public String getEmail(){
        return email;
    }

    public void setEmail(String email) { this.email = email;}
//
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

    public String getBio(){
        return biography;
    }

    public void setBio(String biography) { this.biography = biography;}


}
