package cycloneSounds.Friends;

import cycloneSounds.Credentials.Credentials;
import jakarta.persistence.*;
import cycloneSounds.profilePage.Profile;

@Entity
@Table(name = "Friends")
public class Friends {

    public enum Status {
        PENDING,
        ACCEPTED,
        DECLINED
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Requester", nullable = false)
    private Profile requester;

    @ManyToOne
    @JoinColumn(name = "Receiver", nullable = false)
    private Profile receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;


    public Friends(){
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
