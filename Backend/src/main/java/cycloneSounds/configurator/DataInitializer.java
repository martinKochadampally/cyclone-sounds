/**package cycloneSounds.configurator;

import cycloneSounds.Spotify.SpotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private SpotifyService spotifyService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Application starting... populating initial songs.");

        try {
            // This code will run ONCE, every time the application starts.
            // We are searching for specific artists to get real songs.
            /
            logger.info("Fetching 'Drake'...");
            spotifyService.searchAndSaveTracks("Drake").block();

            logger.info("Fetching 'Taylor Swift'...");
            spotifyService.searchAndSaveTracks("Taylor Swift").block();

            logger.info("Fetching 'SZA'...");
            spotifyService.searchAndSaveTracks("SZA").block();


            logger.info("Initial song population complete.");

        } catch (Exception e) {
            logger.error("Error during initial song population: {}", e.getMessage());
        }
    }
}
 **/