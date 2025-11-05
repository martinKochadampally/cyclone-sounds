package cycloneSounds.Songs;

public class SongDTO {
    private Integer songId;
    private String songName;

    public SongDTO(Integer songId, String songName) {
        this.songId = songId;
        this.songName = songName;
    }

    // getters required for JSON serialization
    public Integer getSongId() { return songId; }
    public String getSongName() { return songName; }
}

