package cycloneSounds.Credentials;

import java.util.List;

import cycloneSounds.profilePage.Profile;
import cycloneSounds.profilePage.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.regex.*;


/**
 *
 * @author Martin Kochadampally
 *
 */
@RestController
public class CredentialController {

    @Autowired
    CredentialRepository credentialRepository;

    @Autowired
    ProfileRepository profileRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping(path = "/login")
    Boolean verifyLogin(@RequestParam String username, @RequestParam String password) {
        Credentials credentials = credentialRepository.findById(username).orElse(null);
        return credentials != null && credentials.getPassword().equals(password);
    }

    @GetMapping(path = "/credentials")
    List<CredentialsDTO> getAllCredentials() {
        return credentialRepository.findAll().stream().map(
                credentials -> new CredentialsDTO(credentials.getUsername(), credentials.getAccountType())
        ).toList();
    }

    @GetMapping(path = "/credentials/{username}")
    CredentialsDTO getCredentialsByUsername( @PathVariable String username){
         return credentialRepository.findById(username).map(
                 credential -> new CredentialsDTO(credential.getUsername(), credential.getAccountType())
         ).orElse(null);
    }

    @PostMapping(path = "/credentials")
    String createCredentials(@RequestParam String username, @RequestParam String password, @RequestParam String accountType) {
        if (credentialRepository.existsById(username)) {
            return failure;
        }

        // If not, create a new Credentials object and save it
        Credentials credential = new Credentials();
        Profile profile = new Profile();
        profile.setUsername(username);
        credential.setUsername(username);
        credential.setPassword(password);
        credential.setAccountType(accountType);

        credentialRepository.save(credential);
        profileRepository.save(profile);
        return success;
    }

    @PutMapping(path = "/credentials/{username}")
    String updateCredentials(@PathVariable String username, @RequestBody Credentials request) {
        if (request == null) {
            return failure;
        } else if (!credentialRepository.existsById(username) || !profileRepository.existsById(username)) {
            return failure;
        }
        Credentials credentials = credentialRepository.findById(username).orElse(null);
        Profile profile = profileRepository.findById(username).orElse(null);
        if (credentials != null && profile != null) {
            if (request.getUsername() != null) {
                credentials.setUsername(request.getUsername());
                profile.setUsername(request.getUsername());
            }
            if (request.getPassword() != null)
                credentials.setPassword(request.getPassword());
            if (request.getAccountType() != null)
                credentials.setAccountType(request.getAccountType());
        }
        credentialRepository.save(credentials);
        profileRepository.save(profile);
        return success;
    }

    @DeleteMapping(path = "/credentials/{username}")
    String deleteCredentials(@PathVariable String username){
        credentialRepository.deleteById(username);
        return success;
    }

    private boolean validateUsername(String username) {
        String regex = "^[A-Za-z][A-Za-z0-9_]{5,29}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}