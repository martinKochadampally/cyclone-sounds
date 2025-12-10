package cycloneSounds.Songs;

/**
 * Song Data Transfer Object
 * Ensures that unnecessary data isn't sent in search get requests.
 *
 * @author Martin Kochadampally
 */
public class SongDTO {
    private Integer songId;
    private String songName;
    private String artist;
    private String spotifyId;

    public SongDTO(Integer songId, String songName, String artist, String spotifyId) {
        this.songId = songId;
        this.songName = songName;
        this.artist = artist;
        this.spotifyId = spotifyId;

    }

    public SongDTO(Song song) {
        this.songId = song.getSongId();
        this.songName = song.getSongName();
        this.artist = song.getArtist();
        this.spotifyId = song.getSpotifyId();
    }

    public Integer getSongId() { return songId; }
    public String getSongName() { return songName; }
    public String getArtist() { return artist; }

    //URL for frontend to use when building embedded spotify player
    public String getEmbedURL()
    {
        if (this.spotifyId != null)
        {
            return "https://open.spotify.com/embed/track/" + this.spotifyId;
        }
        return null;
    }
}

