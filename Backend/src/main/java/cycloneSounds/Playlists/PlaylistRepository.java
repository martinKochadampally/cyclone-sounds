package cycloneSounds.Playlists;


import cycloneSounds.profilePage.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

    List<Playlist> findByUsername(String username);

    List<Playlist> findTop10ByUsernameContainingOrPlaylistNameContainingOrderBySearchesDesc(String username, String playlistName);

}