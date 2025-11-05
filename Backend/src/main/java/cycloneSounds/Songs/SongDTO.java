package cycloneSounds.Songs;

public class SongDTO {
    private Integer songId;
    private String songName;
    private String artist;

    public SongDTO(Integer songId, String songName, String artist) {
        this.songId = songId;
        this.songName = songName;
        this.artist = artist;

    }

    public Integer getSongId() { return songId; }
    public String getSongName() { return songName; }
    public String getArtist() { return artist; }
}

