package cycloneSounds.Jams;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "jams")
public class Jam {
    @Id
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String manager;

    @Getter
    @Setter
    private String approvalType;

    @ElementCollection
    private List<String> members;

    @OneToMany(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private List<JamMessage> messages;

    // Constructors
    public Jam() {
    }

    public Jam(String name, String manager, List<String> members, String approvalType) {
        this.name = name;
        this.manager = manager;
        this.members = members;
        this.approvalType = approvalType;
    }

    // Getters and Setters
    public List<String> getMembers() {
        return members;
    }

    public int getMembersSize() {
        return members != null ? members.size() : 0;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public List<JamMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<JamMessage> messages) {
        this.messages = messages;
    }
}