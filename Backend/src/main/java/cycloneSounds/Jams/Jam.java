package cycloneSounds.Jams;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "jams")
public class Jam {
    @Id
    private String name;

    private String manager;

    @ElementCollection
    private List<String> members;

    @OneToMany(mappedBy = "jam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<JamMessage> messages;

    // Constructors
    public Jam() {
    }

    public Jam(String name, String manager, List<String> members) {
        this.name = name;
        this.manager = manager;
        this.members = members;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public List<String> getMembers() {
        return members;
    }

    public int getMembersSize() {
        return members.size();
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