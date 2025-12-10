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

//    @Query(value = """
//        SELECT * FROM song s
//        WHERE s.id NOT IN (
//            SELECT r.song_id
//            FROM review r
//            WHERE r.reviewer = :reviewer
//        )
//        ORDER BY RAND()
//        LIMIT 1
//        """, nativeQuery = true)
//    Optional<Song> findRandomSongNotReviewedBy(@Param("reviewer") String reviewer);

//    List<Song> findBySongNameContainingIgnoreCase(String name);
}
