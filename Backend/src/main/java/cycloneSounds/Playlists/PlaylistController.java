package cycloneSounds.Playlists;

import cycloneSounds.Songs.Song;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;


/**
 * REST controller for managing user playlists.
 *
 * @author mark
 */
@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    /**
     * Creates a new playlist for a user.
     *
     * @param playlistName Name for the new playlist
     * @param username Username of the playlist owner
     * @return The created Playlist object
     */
    @Operation(
            summary = "Create a new playlist",
            description = "Creates a playlist for a given user and returns the created Playlist object."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Playlist successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input or playlist already exists")
    })
    @PostMapping("/create")
    public ResponseEntity<Playlist> createPlaylist(@RequestParam String playlistName, @RequestParam String username) {
        Playlist playlist = playlistService.createPlaylist(playlistName, username);
        return ResponseEntity.ok(playlist);
    }

    /**
     * Deletes an existing playlist for a user.
     *
     * @param username Username of the playlist owner
     * @param playlistName Name of the playlist to be deleted
     * @return String message on successful deletion
     */
    @Operation(summary = "Delete a playlist",
            description = "Deletes the specified playlist for the given user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Playlist successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Playlist not found")
    })
    @DeleteMapping("/{username}/{playlistName}")
    public ResponseEntity<String> deletePlaylist(@PathVariable String username, @PathVariable String playlistName) {
        playlistService.deletePlaylist(username, playlistName);
        return ResponseEntity.ok("Playlist deleted");
    }

    /**
     * Adds a song to a user's playlist.
     *
     * @param username Username of the playlist owner
     * @param playlistName Playlist to which the song is added
     * @param songName Name of the song to add
     * @param artist Artist of the song to add
     * @return Playlist object after the song was added
     */
    @Operation(summary = "Add song to playlist",
            description = "Adds a specified song (by name and artist) to a given user's playlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song successfully added to playlist"),
            @ApiResponse(responseCode = "404", description = "Playlist not found or song not found")
    })
    @PostMapping("/{username}/{playlistName}/add")
    public ResponseEntity<Playlist> addSongToPlaylist(@PathVariable String username, @PathVariable String playlistName, @RequestParam String songName, @RequestParam String artist) {
        try {
            Playlist playlist = playlistService.addSongToPlaylist(username, playlistName, songName, artist);
            return ResponseEntity.ok(playlist);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    /**
     * Removes a song from a user's playlist.
     *
     * @param username Username of playlist owner
     * @param playlistName Playlist to remove from
     * @param songName Name of the song to remove
     * @param artist Artist of the song to remove
     * @return Playlist object after the song was removed
     */
    @Operation(summary = "Remove song from playlist",
            description = "Removes a specified song (by name and artist) from a given user's playlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Song successfully removed from playlist"),
            @ApiResponse(responseCode = "404", description = "Playlist or song not found")
    })
    @DeleteMapping("/{username}/{playlistName}/remove")
    public ResponseEntity<Playlist> removeSongFromPlaylist(@PathVariable String username, @PathVariable String playlistName, @RequestParam String songName, @RequestParam String artist) {
        Playlist playlist = playlistService.removeSongFromPlaylist(username, playlistName, songName, artist);
        return ResponseEntity.ok(playlist);
    }

    /**
     * Gets all playlists owned by a user.
     *
     * @param username Username whose playlists are retrieved
     * @return List of all playlists belonging to the user
     */
    @Operation(summary = "Get playlists by owner",
            description = "Retrieves all playlists belonging to the specified user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Playlists successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "No playlists found for user")
    })
    @GetMapping("/owner/{username}")
    public ResponseEntity<List<Playlist>> getPlaylistsByOwner(@PathVariable String username) {
        List<Playlist> playlists = playlistService.getPlaylistsByOwner(username);
        return ResponseEntity.ok(playlists);
    }

    /**
     * Gets all songs from a user's specific playlist.
     *
     * @param username Username of playlist owner
     * @param playlistName Playlist name for the song list
     * @return Set of Song objects in the specified playlist
     */
    @Operation(summary = "Get songs from playlist",
            description = "Retrieves all songs in a user's given playlist")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Songs successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Playlist not found")
    })
    @GetMapping("/{username}/{playlistName}/songs")
    public ResponseEntity<Set<Song>> getSongsFromPlaylist(@PathVariable String username, @PathVariable String playlistName) {
        Set<Song> songs = playlistService.getSongsFromPlaylist(username, playlistName);
        return ResponseEntity.ok(songs);
    }
}