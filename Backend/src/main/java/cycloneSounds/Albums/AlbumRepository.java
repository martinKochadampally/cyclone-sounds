package cycloneSounds.Albums;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {

    List<Album> findByTitleContaining(String title);

    Album findBySpotifyId(String spotifyId);
}