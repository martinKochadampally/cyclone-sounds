package cycloneSounds.Credentials;

public class CredentialsDTO {
    private String emailId;
    private String username;
    private String accountType;

    public CredentialsDTO() {}

    public CredentialsDTO(String emailId, String username, String accountType) {
        this.emailId = emailId;
        this.username = username;
        this.accountType = accountType;
    }

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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
