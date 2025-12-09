package cycloneSounds.Albums;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {

    Album findBySpotifyId(String spotifyId);
    List<Album> findByTitleContainingIgnoreCase(String name);
}