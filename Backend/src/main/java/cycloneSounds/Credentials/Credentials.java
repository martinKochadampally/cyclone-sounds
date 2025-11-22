package cycloneSounds.Credentials;

import jakarta.persistence.*;

import cycloneSounds.profilePage.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**
 *
 * @author Martin Kochadampally
 *
 */
@Entity
public class Credentials {

    @Id
    @Column(unique = true)
    @NotBlank
    @Size(min = 6, max = 30)
    private String username;

    @NotBlank
    @Size(min = 8, max = 64)
    private String password;

    @NotBlank
    private String accountType;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "username")
    private Profile profile;

    public Credentials(String username, String password, String accountType) {
        this.username = username;
        this.password = password;
        this.accountType = accountType;
    }

    public Credentials() {}

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