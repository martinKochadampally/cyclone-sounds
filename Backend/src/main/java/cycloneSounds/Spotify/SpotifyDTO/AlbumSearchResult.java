package cycloneSounds.Spotify.SpotifyDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumSearchResult {

    private List<SpotifyAlbum> items;

    public List<SpotifyAlbum> getItems() {
        return items;
    }

    public void setItems(List<SpotifyAlbum> items) {
        this.items = items;
    }
}