package cycloneSounds.Albums;

import cycloneSounds.Spotify.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/albums")
@RestController
public class AlbumController {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SpotifyService spotifyService;

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
        return albumRepository.findById(id).orElse(null);
    }

    @GetMapping("/spotify/{spotifyId}")
    public Album getAlbumBySpotifyId(@PathVariable String spotifyId) {
        return albumRepository.findBySpotifyId(spotifyId);
    }
}