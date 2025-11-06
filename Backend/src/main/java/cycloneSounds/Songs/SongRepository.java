package cycloneSounds.Songs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Integer> {

    Optional<Song> findBySpotifyId(String spotifyId);

    Optional<Song> findBySongNameAndArtist(String songName, String artist);

    List<Song> findTop15ByArtistContainingOrSongNameContainingOrderBySearchesDesc(String artist, String songName);
}
