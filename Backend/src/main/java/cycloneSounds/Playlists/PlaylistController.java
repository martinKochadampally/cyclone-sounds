package cycloneSounds.Playlists;

import cycloneSounds.Songs.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    /**
     * Post mapping for playlist creation
     * @param playlistName
     * @param username
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<Playlist> createPlaylist(@RequestParam String playlistName, @RequestParam String username) {
        Playlist playlist = playlistService.createPlaylist(playlistName, username);
        return ResponseEntity.ok(playlist);
    }

    /**
     * Delete mapping to allow playlists to be deleted
     * @param username
     * @param playlistName
     * @return
     */
    @DeleteMapping("/{username}/{playlistName}")
    public ResponseEntity<String> deletePlaylist(@PathVariable String username, @PathVariable String playlistName) {
        playlistService.deletePlaylist(username, playlistName);
        return ResponseEntity.ok("Playlist deleted");
    }

    /**
     * Post mapping that allows users to add songs to playlists
     * @param username
     * @param playlistName
     * @param songName
     * @param artist
     * @return
     */
    @PostMapping("/{username}/{playlistName}/add")
    public ResponseEntity<Playlist> addSongToPlaylist(@PathVariable String username, @PathVariable String playlistName, @RequestParam String songName, @RequestParam String artist) {
        Playlist playlist = playlistService.addSongToPlaylist(username, playlistName, songName, artist);
        return ResponseEntity.ok(playlist);
    }

    /**
     * Delete mapping that allows users to delete songs from playlists
     * @param username
     * @param playlistName
     * @param songName
     * @param artist
     * @return
     */
    @DeleteMapping("/{username}/{playlistName}/remove")
    public ResponseEntity<Playlist> removeSongFromPlaylist(@PathVariable String username, @PathVariable String playlistName, @RequestParam String songName, @RequestParam String artist) {
        Playlist playlist = playlistService.removeSongFromPlaylist(username, playlistName, songName, artist);
        return ResponseEntity.ok(playlist);
    }

    /**
     * Get request that finds playlist based off a username
     * @param username
     * @return
     */
    @GetMapping("/owner/{username}")
    public ResponseEntity<List<Playlist>> getPlaylistsByOwner(@PathVariable String username) {
        List<Playlist> playlists = playlistService.getPlaylistsByOwner(username);
        return ResponseEntity.ok(playlists);
    }

    /**
     * Get request that gets all the songs from a specific users playlist.
     * @param username
     * @param playlistName
     * @return
     */
    @GetMapping("/{username}/{playlistName}/songs")
    public ResponseEntity<Set<Song>> getSongsFromPlaylist(@PathVariable String username, @PathVariable String playlistName) {
        Set<Song> songs = playlistService.getSongsFromPlaylist(username, playlistName);
        return ResponseEntity.ok(songs);
    }


}