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
    public ResponseEntity<Playlist> createPlaylist(@RequestParam String playlistName, @RequestParam String username) {
        Playlist playlist = playlistService.createPlaylist(playlistName, username);
        return ResponseEntity.ok(playlist);
    }

    @DeleteMapping("/{username}/{playlistName}")
    public ResponseEntity<String> deletePlaylist(@PathVariable String username, @PathVariable String playlistName) {
        playlistService.deletePlaylist(username, playlistName);
        return ResponseEntity.ok("Playlist deleted");
    }

    @PostMapping("/{username}/{playlistName}/add")
    public ResponseEntity<Playlist> addSongToPlaylist(@PathVariable String username, @PathVariable String playlistName, @RequestParam String songName, @RequestParam String artist) {
        Playlist playlist = playlistService.addSongToPlaylist(username, playlistName, songName, artist);
        return ResponseEntity.ok(playlist);
    }

    @DeleteMapping("/{username}/{playlistName}/remove")
    public ResponseEntity<Playlist> removeSongFromPlaylist(@PathVariable String username, @PathVariable String playlistName, @RequestParam String songName, @RequestParam String artist) {
        Playlist playlist = playlistService.removeSongFromPlaylist(username, playlistName, songName, artist);
        return ResponseEntity.ok(playlist);
    }

    @GetMapping("/owner/{username}")
    public ResponseEntity<List<Playlist>> getPlaylistsByOwner(@PathVariable String username) {
        List<Playlist> playlists = playlistService.getPlaylistsByOwner(username);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{username}/{playlistName}/songs")
    public ResponseEntity<Set<Song>> getSongsFromPlaylist(@PathVariable String username, @PathVariable String playlistName) {
        Set<Song> songs = playlistService.getSongsFromPlaylist(username, playlistName);
        return ResponseEntity.ok(songs);
    }


}