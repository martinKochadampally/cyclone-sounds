package cycloneSounds.Reviews;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Integer> {

    //Optional<Song> findByReviewerAndSongNameAndArtistAndRatingAndBody(String reviewer, String songName, String artist, double rating, String body);
    Optional<Song> findBySongNameAndArtist(String songName, String artist);
}
