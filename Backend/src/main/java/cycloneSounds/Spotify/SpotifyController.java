package cycloneSounds.Spotify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    /**
     * Post mapping that allows songs to be added to the database
     * Only for backend population of the table
     * @param query
     * @return
     */
    @PostMapping("/api/songs/search")
    public Mono<String> searchAndSave(@RequestParam String query) {
        return spotifyService.searchAndSaveTracks(query)
                .then(Mono.just("Search complete. New songs added to database."))
                .onErrorResume(e -> Mono.just("Error during search: " + e.getMessage()));
    }
}