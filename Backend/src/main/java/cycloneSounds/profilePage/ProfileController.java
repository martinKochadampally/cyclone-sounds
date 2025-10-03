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
 * 
 * @author Mark Seward
 * 
 */

/**
 * A REST controller for handling HTTP requests related to Profiles.
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
     * @param email
     * @return the information for the given email
     */
    @GetMapping(path = "/profiles/{email}")
    public Profile getProfilebyEmail(@PathVariable String email) {
        return profileRepository.findByEmail(email).orElse(null);
    }
    /**
     * POST endpoint to create a new profile. (Create)
     *
     * @param profile The Profile object sent in the request body.
     * @return A JSON string indicating success or failure.
     */
    @PostMapping(path = "/profiles")
    public String createProfile(@RequestBody Profile profile) {
        if (profile == null) {
            return failure;
        }
        profileRepository.save(profile);
        return success;
    }

    /**
     * PUT endpoint to update an existing profile. (Update)
     *
     * @param email      The ID of the profile to update.
     * @param request The Profile object containing the new data.
     * @return The updated Profile object, or null if the profile wasn't found.
     */
    @PutMapping("/profiles/{email}")
    public Profile updateProfile(@PathVariable String email, @RequestBody Profile request) {
        Optional<Profile> profileOptional = profileRepository.findByEmail(email);
        if (profileOptional.isEmpty()) {
            return null;
        }
        Profile profile = profileOptional.get();

        // Update fields from the request
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile.setFavSong(request.getFavSong());
        profile.setFavArtist(request.getFavArtist());
        profile.setFavGenre(request.getFavGenre());
        profile.setBiography(request.getBiography());

        profileRepository.save(profile);
        return profile;
    }

    /**
     * DELETE endpoint to remove a profile by its email. (Delete)
     *
     * @param email The name of the profile to delete.
     * @return A JSON string indicating success.
     */
    @Transactional
    @DeleteMapping(path = "/profiles/{email}")
    public String deleteProfile(@PathVariable String email) {
        // Check if the profile exists before deleting
        if (profileRepository.existsByEmail(email)) {
            profileRepository.deleteByEmail(email);
            return success;
        }
        return failure;
    }
}