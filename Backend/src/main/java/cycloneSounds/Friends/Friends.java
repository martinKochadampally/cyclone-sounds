package cycloneSounds.Friends;

import cycloneSounds.Credentials.Credentials;
import jakarta.persistence.*;
import org.springframework.context.annotation.Profile;

public class Friends {

    public enum Status {
        PENDING,
        ACCEPTED,
        DECLINED
    }

    @Id
    private String username;

    @ManyToOne
    @JoinColumn(name = "requesterUsername", nullable = false)
    private Profile requester;

    @ManyToOne
    @JoinColumn(name = "receiverUsername", nullable = false)
    private Profile receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


    public Friends(){

    }
    public String getUsername(){
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Profile getRequester(){
        return requester;
    }

    public void setRequester(Profile requester){
        this.requester = requester;
    }

    public Profile getReceiver(){
        return receiver;
    }

    public void setReceiver(Profile receiver){
        this.receiver = receiver;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
