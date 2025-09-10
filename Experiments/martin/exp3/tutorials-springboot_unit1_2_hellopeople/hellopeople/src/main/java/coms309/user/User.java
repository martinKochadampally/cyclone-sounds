package coms309.user;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Provides the Definition/Structure for the people row
 *
 * @author Martin Kochadampally
 */
@Getter // Lombok Shortcut for generating getter methods (Matches variable names set ie firstName -> getFirstName)
@Setter // Similarly for setters as well
@NoArgsConstructor // Default constructor
public class User {

    private String email;

    private String firstName;

    private String lastName;

    private String telephone;

    private String[] favouriteSongs;

    private String[] producedSongs;

//    public User(){
//
//    }

    public User(String email, String firstName, String lastName, String telephone, String[] favouriteSongs, String[] producedSongs){
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telephone = telephone;
        this.favouriteSongs = favouriteSongs;
        this.producedSongs = producedSongs;

    }


    /**
     * Getter and Setters below are technically redundant and can be removed.
     * They will be generated from the @Getter and @Setter tags above class
     */

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String[] getFavouriteSongs() {
        return this.favouriteSongs;
    }

    public void setFavouriteSongs(String[] favouriteSongs) {
        this.favouriteSongs = favouriteSongs;
    }

    public String[] getProducedSongs() {
        return this.producedSongs;
    }

    public void setProducedSongs(String[] producedSongs) {
        this.producedSongs = producedSongs;
    }

    @Override
    public String toString() {
        return email + " "
                + firstName + " "
                + lastName + " "
                + telephone + " "
                + favouriteSongs.toString() + " "
                + producedSongs.toString();
    }
}
