package cycloneSounds.Credentials;

import jakarta.persistence.*;

//import cyclone-sounds.profilePage.Profile;


/**
 *
 * @author Martin Kochadampally
 *
 */
@Entity
public class Credentials {

    @Id
    @Column(length = 50)
    private String emailId;

    @Column(unique = true)
    private String username;

    private String password;

    private String accountType;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "emailID")
//    private Profile profile;

    public Credentials(String emailId, String username, String password, String accountType) {
        this.emailId = emailId;
        this.username = username;
        this.password = password;
        this.accountType = accountType;
    }

    public Credentials() {}

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}