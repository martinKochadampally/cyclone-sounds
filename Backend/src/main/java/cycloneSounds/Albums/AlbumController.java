package cycloneSounds.Albums;

import cycloneSounds.Spotify.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/albums")
@RestController
public class AlbumController {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/{spotifyAlbumId}/sync")
    public ResponseEntity<Album> syncAlbum(@PathVariable String spotifyAlbumId) {
        Album album = spotifyService.syncAlbumBySpotifyId(spotifyAlbumId);
        return ResponseEntity.ok(album);
    }
    @GetMapping("/{spotifyAlbumId}")
    public ResponseEntity<Album> getAlbumAndSongs(@PathVariable String spotifyAlbumId) {
        Album album = spotifyService.syncAlbumBySpotifyId(spotifyAlbumId);

        if (album != null) {
            return ResponseEntity.ok(album);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping(path = "/search/{name}")
    public List<Album> searchAlbum(@PathVariable String name) {
        return albumRepository.findByTitleContainingIgnoreCase(name);
    }

    @GetMapping(path = "/")
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    @GetMapping(path = "/{id}")
    public Album getAlbumById(@PathVariable int id) {
        Album album = albumRepository.findById(id).orElse(null);
        if (album == null){
            return null;
        }
        if(album.getSongs() == null || album.getSongs().isEmpty()){
            spotifyService.populateAlbumTracks(album);
            album = albumRepository.findById(id).orElse(null);
        }
        return album;
    }

    @GetMapping("/spotify/{spotifyId}")
    public Album getAlbumBySpotifyId(@PathVariable String spotifyId) {
        return albumRepository.findBySpotifyId(spotifyId);
    }
}