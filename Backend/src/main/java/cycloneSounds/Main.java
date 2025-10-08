package cycloneSounds;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import cycloneSounds.Credentials.Credentials;
import cycloneSounds.Credentials.CredentialRepository;

import cycloneSounds.profilePage.Profile;
import cycloneSounds.profilePage.ProfileRepository;

/**
 * @author Vivek Bengre
 */
@SpringBootApplication
@EnableJpaRepositories
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
        return null;
    }
}

