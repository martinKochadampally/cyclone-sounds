package cycloneSounds.Spotify.SpotifyDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Artist class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyArtist {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}