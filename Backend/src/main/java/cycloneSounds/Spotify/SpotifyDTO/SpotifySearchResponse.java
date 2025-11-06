package cycloneSounds.Spotify.SpotifyDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class for search query
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifySearchResponse {

    private TrackSearchResult tracks;

    public TrackSearchResult getTracks() {
        return tracks;
    }

    public void setTracks(TrackSearchResult tracks) {
        this.tracks = tracks;
    }
}