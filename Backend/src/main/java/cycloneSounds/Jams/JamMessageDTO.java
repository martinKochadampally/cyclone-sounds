package cycloneSounds.Jams;

import lombok.Getter;
import lombok.Setter;

public class JamMessageDTO {
    @Getter
    @Setter
    private String content;

    public JamMessageDTO() {
    }

    public JamMessageDTO(String content) {
        this.content = content;
    }
}
