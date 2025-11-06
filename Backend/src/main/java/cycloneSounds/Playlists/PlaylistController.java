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

    @PostMapping("/create")
    public ResponseEntity<Playlist> createPlaylist(@RequestParam String playlistName, @RequestParam String ownerUsername) {
        Playlist playlist = playlistService.createPlaylist(playlistName, ownerUsername);
        return ResponseEntity.ok(playlist);
    }

    @DeleteMapping("/{playlistName}")
    public ResponseEntity<String> deletePlaylist(@PathVariable String playlistName) {
        playlistService.deletePlaylist(playlistName);
        return ResponseEntity.ok("Playlist deleted");
    }

    @PostMapping("/{playlistName}/add")
    public ResponseEntity<Playlist> addSongToPlaylist(@PathVariable String playlistName, @RequestParam int songId) {
        Playlist playlist = playlistService.addSongToPlaylist(playlistName, songId);
        return ResponseEntity.ok(playlist);
    }

    @PostMapping("/{playlistName}/remove")
    public ResponseEntity<Playlist> removeSongFromPlaylist(@PathVariable String playlistName, @RequestParam int songId) {
        Playlist playlist = playlistService.removeSongFromPlaylist(playlistName, songId);
        return ResponseEntity.ok(playlist);
    }

    @GetMapping("/owner/{ownerUsername}")
    public ResponseEntity<List<Playlist>> getPlaylistsByOwner(@PathVariable String ownerUsername) {
        List<Playlist> playlists = playlistService.getPlaylistsByOwner(ownerUsername);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{playlistName}/songs")
    public ResponseEntity<Set<Song>> getSongsFromPlaylist(@PathVariable String playlistName) {
        Set<Song> songs = playlistService.getSongsFromPlaylist(playlistName);
        return ResponseEntity.ok(songs);
    }
}