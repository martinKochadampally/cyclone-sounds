package cycloneSounds.Playlists;

import lombok.Getter;
import lombok.Setter;

public class PlaylistDTO {
    @Getter
    @Setter
    private String playlistName;

    @Getter
    @Setter
    private String username;

    public PlaylistDTO(String playlistName, String username) {
        this.playlistName = playlistName;
        this.username = username;
    }
}
