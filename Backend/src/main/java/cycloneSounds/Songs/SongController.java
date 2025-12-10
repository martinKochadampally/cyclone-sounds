package cycloneSounds.Songs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SongController {

    @Autowired
    private SongRepository songRepository;

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
                .map(song -> new SongDTO(song.getSongId(), song.getSongName(), song.getArtist(), song.getSpotifyId()))
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

}