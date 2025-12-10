package cycloneSounds.AlbumReview;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlbumReviewRepository extends JpaRepository<AlbumReview, Integer> {

    List<AlbumReview> findByAlbumReviewer(String albumReviewer);
    List<AlbumReview> findByAlbum_AlbumId(int albumId);
}