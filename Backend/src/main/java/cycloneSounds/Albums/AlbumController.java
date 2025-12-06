package cycloneSounds.Albums;

import cycloneSounds.Spotify.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AlbumController {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping(path = "/albums/search/{name}")
    public Album searchAlbum(@PathVariable String name) {
        return spotifyService.searchAndSaveAlbum(name).block();
    }

    @GetMapping(path = "/albums")
    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    @GetMapping(path = "/albums/{id}")
    public Album getAlbumById(@PathVariable int id) {
        return albumRepository.findById(id).orElse(null);
    }
}