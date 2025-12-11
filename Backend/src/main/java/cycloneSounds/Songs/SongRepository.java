package cycloneSounds.Songs;

import cycloneSounds.Songs.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Integer> {

    Optional<Song> findBySpotifyId(String spotifyId);

    Optional<Song> findBySongNameAndArtist(String songName, String artist);

    List<Song> findTop15ByArtistContainingOrSongNameContainingOrderBySearchesDesc(String artist, String songName);

    Optional<Song> findBySongId(int songId);

}
