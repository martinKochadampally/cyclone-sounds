package cycloneSounds.Search;

import cycloneSounds.Playlists.*;
import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongDTO;
import cycloneSounds.Songs.SongRepository;
import cycloneSounds.profilePage.Profile;
import cycloneSounds.profilePage.ProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    PlaylistRepository playlistRepository;

    /**
     * Searches for user profiles where the username contains the search key,
     * returning top 10 results ordered by views descending.
     *
     * @param searchKey The substring to search for in usernames
     * @return List of matching usernames
     */
    @Operation(summary = "Search user profiles by username",
            description = "Returns up to 10 usernames containing the search key, ordered by views.")
    @ApiResponse(responseCode = "200", description = "List of matching usernames")
    @GetMapping("/search/profiles/{searchKey}")
    public List<String> getProfileByName(@PathVariable String searchKey) {
            return profileRepository.findTop10ByUsernameContainingOrderByViewsDesc(searchKey)
            .stream()
            .map(Profile::getUsername) // extract just the username string
            .collect(Collectors.toList());
    }

    /**
     * Increments the view count of a profile identified by username,
     * then returns the updated Profile object.
     *
     * @param username Username of the profile to update
     * @return Updated Profile object or null if not found
     */
    @Operation(summary = "Increment profile view count by username",
            description = "Increments view count for specified user profile and returns updated profile.")
    @ApiResponse(responseCode = "200", description = "Updated profile returned")
    @PutMapping(path = "search/profiles/{username}")
    public Profile updateProfileViewsbyUsername(@PathVariable String username) {
        Profile res = profileRepository.findById(username).orElse(null);
        if (res != null) {
            res.incrementViews();
            profileRepository.save(res);
        }
        return res;
    }

    /**
     * Searches for songs by artist or song name containing the search key,
     * returning up to 15 results ordered by searches descending.
     *
     * @param searchKey The substring to search for in artist or song names
     * @return List of SongDTO objects with song ID, name, and artist
     */
    @Operation(summary = "Search songs by artist or name",
            description = "Returns up to 15 songs matching search key, ordered by number of searches.")
    @ApiResponse(responseCode = "200", description = "List of matching song data transfer objects")
    @GetMapping("/search/songs/{searchKey}")
    public List<SongDTO> getSongsByName(@PathVariable String searchKey) {
        return songRepository.findTop15ByArtistContainingOrSongNameContainingOrderBySearchesDesc(searchKey, searchKey)
                .stream()
                .map(song -> new SongDTO(song.getSongId(), song.getSongName(), song.getArtist()))
                .collect(Collectors.toList());
    }

    /**
     * Increments the search count of a song identified by songID,
     * then returns the updated Song object.
     *
     * @param songID ID of the song to update searches
     * @return Updated Song object or null if not found
     */
    @Operation(summary = "Increment song search count by song ID",
            description = "Increments search count for specified song and returns updated song.")
    @ApiResponse(responseCode = "200", description = "Updated song returned")
    @PutMapping(path = "search/songs/{songID}")
    public Song updateSongSearchesbySongID(@PathVariable int songID) {
        Song res = songRepository.findById(songID).orElse(null);
        if (res != null) {
            res.incrementSearches();
            songRepository.save(res);
        }
        return res;
    }

    /**
     * Searches for playlists by username or playlist name containing the search key,
     * returning up to 10 results ordered by searches descending.
     *
     * @param searchKey Substring to search in usernames or playlist names
     * @return List of PlaylistDTO objects containing playlist name and username of creator
     */
    @Operation(summary = "Search playlists by username or playlist name",
            description = "Returns up to 10 playlists matching search key, ordered by number of searches.")
    @ApiResponse(responseCode = "200", description = "List of matching playlist data transfer objects")
    @GetMapping("/search/playlist/{searchKey}")
    public List<PlaylistDTO> getPlaylistByName(@PathVariable String searchKey) {
        return playlistRepository.findTop10ByUsernameContainingOrPlaylistNameContainingOrderBySearchesDesc(searchKey, searchKey)
                .stream()
                .map(playlist -> new PlaylistDTO(playlist.getPlaylistName(), playlist.getUsername()))
                .collect(Collectors.toList());
    }

    /**
     * Increments the search count of a playlist identified by playlistName,
     * then returns the updated Playlist object.
     *
     * @param playlistName Name of the playlist to update searches
     * @return Updated Playlist object or null if not found
     */
    @Operation(summary = "Increment playlist search count by playlist name",
            description = "Increments search count for specified playlist and returns updated playlist.")
    @ApiResponse(responseCode = "200", description = "Updated playlist returned")
    @PutMapping(path = "search/playlist/{playlistName}")
    public Playlist updatePlaylistSearchesByPlaylistName(@PathVariable String playlistName) {
        Playlist res = playlistRepository.findById(playlistName).orElse(null);
        if (res != null) {
            res.incrementSearches();
            playlistRepository.save(res);
        }
        return res;
    }
}
