package cycloneSounds;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import cycloneSounds.profilePage.Profile;
import cycloneSounds.profilePage.ProfileRepository;

/**
 * @author Vivek Bengre
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    /**
     * Creates a CommandLineRunner bean to insert dummy data into the database upon application startup.
     * This is useful for testing and demonstration purposes.
     *
     * @param profileRepository The repository for the Profile entity.
     * @return A CommandLineRunner that creates and saves three sample profiles.
     */
    @Bean
    CommandLineRunner initProfiles(ProfileRepository profileRepository) {
        return args -> {
            // Create three sample Profile objects
            Profile profile1 = new Profile("Mark", "Mark@iastate.edu", "Bohemian Rhapsody", "Queen", "Rock", "Loves classic rock and coding.");
            Profile profile2 = new Profile("Martin", "martin@gmail.com", "Blinding Lights", "The Weeknd", "Synth-pop", "Enjoys modern pop music and design.");
            Profile profile3 = new Profile("Jack", "Jack@yahoo.com", "Take Five", "Dave Brubeck Quartet", "Jazz", "A fan of jazz music and photography.");

            // Save the profiles to the database
            profileRepository.save(profile1);
            profileRepository.save(profile2);
            profileRepository.save(profile3);
        };
    }
}

