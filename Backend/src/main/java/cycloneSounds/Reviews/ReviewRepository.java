package cycloneSounds.Reviews;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

        List<Review> findByReviewer(String reviewer);
        List<Review> findById(int songId);
        List<Review> findBySong_SongId(int songId);
        //List<Review> findByArtist(String artist);
        //Optional<Song> findBySongNameAndArtist(String review, String songName, String artist);
}