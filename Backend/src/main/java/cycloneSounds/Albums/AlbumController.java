package cycloneSounds.Albums;

import cycloneSounds.Spotify.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * A controller using REStful API which manages music Albums in the system.
 * AlbumController handles the database retireivals and integeration with the SpotifyService
 * which helps match album data and track info from teh Spotify API.
 * @author Mark Seward
 */
@RequestMapping("/albums")
@RestController
public class AlbumController {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SpotifyService spotifyService;

    /**
     * Retrieves an album using the external SpotifyID. Asks the SpotifyService to find album data
     * from SPotify and save it to the database
     * @param spotifyAlbumId
     * @return ResponseEntity if album is found, or 404 not found if it cant be found
     */
    @Operation(summary = "Sync and retrieve album via Spotify ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album successfully synced and retrieved"),
            @ApiResponse(responseCode = "404", description = "Album not found on Spotify")
    })
    @GetMapping("/{spotifyAlbumId}")
    public ResponseEntity<Album> getAlbumAndSongs(@PathVariable String spotifyAlbumId) {
        Album album = spotifyService.syncAlbumBySpotifyId(spotifyAlbumId);

        if (album != null) {
            return ResponseEntity.ok(album);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Searches for albums in the tables that have the name
     * @param name
     * @return List of albums
     */
    @Operation(summary = "Search albums by title")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of albums matching the search term")
    })
    @GetMapping(path = "/search/{name}")
    public List<Album> searchAlbum(@PathVariable String name) {
        return albumRepository.findByTitleContainingIgnoreCase(name);
    }

    /**
     * grabs the list of all albums in the SQL tables
     * @return list of all ALbum objects
     */
    @Operation(summary = "Get all albums")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all albums in the database")
    })
    @GetMapping(path = "/")
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    /**
     * Gets an album by the internal id which is the primary key
     * If the album is not populated, the SpotifyService is triggered to retrieve all songs in the album
     * @param id
     * @return album object or null
     */
    @Operation(summary = "Get album by internal database ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album found (songs populated if missing)"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    @GetMapping(path = "/database/{id}")
    public Album getAlbumById(@PathVariable int id) {
        Album album = albumRepository.findById(id).orElse(null);

        if (album == null) {
            return null;
        }

        boolean missingSongs = (album.getSongs() == null || album.getSongs().isEmpty());

        if (missingSongs) {
            spotifyService.populateAlbumTracks(album);
            album = albumRepository.findById(id).orElse(null);
        }

        return album;
    }

    /**
     * Retrieves an album based on the spotifyID from the SQL database.
     * Does not update songs in the album from the API if not populated
     * @param spotifyId
     * @return album object or null
     */
    @Operation(summary = "Get local album by Spotify ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album found in local database")
    })
    @GetMapping("/spotify/{spotifyId}")
    public Album getAlbumBySpotifyId(@PathVariable String spotifyId) {
        return albumRepository.findBySpotifyId(spotifyId);
    }
}