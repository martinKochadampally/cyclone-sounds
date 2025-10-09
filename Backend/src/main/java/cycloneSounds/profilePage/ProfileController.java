package cycloneSounds.profilePage;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * A REST controller for handling HTTP requests related to Profiles.
 *
 * @author Mark Seward
 *
 */
@RestController
public class ProfileController {

    @Autowired
    ProfileRepository profileRepository;

    private final String success = "{\"message\":\"success\"}";
    private final String failure = "{\"message\":\"failure\"}";

    /**
     * GET endpoint to list all profiles. (List)
     *
     * @return A List of all Profile objects in the database.
     */
    @GetMapping(path = "/profiles")
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    /**
     * GET endpoint
     * @param username
     * @return the information for the given username
     */
    @GetMapping(path = "/profiles/{username}")
    public Profile getProfilebyUsername(@PathVariable String username) {
        return profileRepository.findById(username).orElse(null);
    }
    /**
     * POST endpoint to create a new profile. (Create)
     *
     * @param profile The Profile object sent in the request body.
     * @return A JSON string indicating success or failure.
     */
    @PostMapping(path = "/profiles")
    public String createProfile(@RequestBody Profile profile) {
        if (profile == null || profile.getUsername() == null || profile.getName() == null || profile.getFavArtist() == null ||
            profile.getFavGenre() == null || profile.getFavSong() == null) {
            return failure;
        }
        profileRepository.save(profile);
        return success;
    }

    /**
     * PUT endpoint to update an existing profile. (Update)
     *
     * @param username      The ID of the profile to update.
     * @param request The Profile object containing the new data.
     * @return The updated Profile object, or null if the profile wasn't found.
     */
    @PutMapping("/profiles/{username}")
    public Profile updateProfile(@PathVariable String username, @RequestBody Profile request) {
        Optional<Profile> profileOptional = profileRepository.findById(username);
        if (profileOptional.isEmpty()) {
            return null; // User not found
        }

        Profile profileToUpdate = profileOptional.get();

        // Update all fields EXCEPT the primary key (username)
        profileToUpdate.setName(request.getName());
        profileToUpdate.setFavSong(request.getFavSong());
        profileToUpdate.setFavArtist(request.getFavArtist());
        profileToUpdate.setFavGenre(request.getFavGenre());
        profileToUpdate.setBiography(request.getBiography());

        profileRepository.save(profileToUpdate);
        return profileToUpdate;
    }

    /**
     * DELETE endpoint to remove a profile by its username. (Delete)
     *
     * @param username The name of the profile to delete.
     * @return A JSON string indicating success.
     */
    @Transactional
    @DeleteMapping(path = "/profiles/{username}")
    public String deleteProfile(@PathVariable String username) {
        // Check if the profile exists before deleting
        if (profileRepository.existsById(username)) {
            profileRepository.deleteById(username);
            return success;
        }
        return failure;
    }
}