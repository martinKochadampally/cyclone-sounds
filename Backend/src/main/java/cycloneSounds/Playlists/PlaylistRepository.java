package cycloneSounds.Playlists;

import cycloneSounds.Reviews.Song;
import cycloneSounds.profilePage.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Song, String> {

}
