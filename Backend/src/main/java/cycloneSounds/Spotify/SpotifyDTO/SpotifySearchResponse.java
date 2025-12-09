package cycloneSounds.Spotify.SpotifyDTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Class for search query
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifySearchResponse {

    private TrackSearchResult tracks;
    private AlbumSearchResult albums;

    public TrackSearchResult getTracks() {
        return tracks;
    }

    public void setTracks(TrackSearchResult tracks) {
        this.tracks = tracks;
    }

    public AlbumSearchResult getAlbums() {
        return albums;
    }
    public void setAlbums(AlbumSearchResult albums) {
        this.albums = albums;
    }

}