package cycloneSounds.Spotify.SpotifyDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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