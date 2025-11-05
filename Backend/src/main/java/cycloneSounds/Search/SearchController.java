package cycloneSounds.Search;

import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import cycloneSounds.profilePage.Profile;
import cycloneSounds.profilePage.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * A REST controller for handling HTTP requests related to Searching.
 *
 * @author Martin Kochadampally
 *
 */
@RestController
public class SearchController {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    SongRepository songRepository;

    /**
     * GET endpoint to search for other user's profiles.
     *
     * @param searchKey
     * @return
     */
    @GetMapping("/search/profiles/{searchKey}")
    public List<Profile> getProfileByName(@PathVariable String searchKey) {
        return profileRepository.findTop10ByUsernameContainingOrderByViewsDesc(searchKey);
    }

    /**
     * GET endpoint to search for songs by their names or artist.
     *
     * @param searchKey
     * @return
     */
    @GetMapping("/search/songs/{searchKey}")
    public List<Song> getSongsByName(@PathVariable String searchKey) {
        return songRepository.findTop15ByArtistContainingOrSongNameContainingOrderBySearchesDesc(searchKey, searchKey);
    }

    /**
     * GET endpoint to search for playlists by name.
     *
     * @param searchKey
     * @return
     */
//    @GetMapping("/search/playlist/{searchKey}")
//    public List<Playlist> getPlaylistByName(@PathVariable String searchKey) {
//        return playlistRepository.findTop15ByNameContainingOrderByViewsDesc(searchKey);
//    }
}
