package cycloneSounds.Credentials;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 *
 * @author Martin Kochadampally
 *
 */
@RestController
public class CredentialController {

    @Autowired
    CredentialRepository credentialRepository;

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
        credential.setUsername(username);
        credential.setPassword(password);
        credential.setAccountType(accountType);

        credentialRepository.save(credential);
        return success;
    }

    @PutMapping(path = "/credentials/{username}")
    String updateCredentials(@PathVariable String username, @RequestBody Credentials request) {
        if (request == null || request.getUsername() == null ||
                request.getPassword() == null || request.getAccountType() == null) {
            return failure;
        } else if (!credentialRepository.existsById(username)) {
            return failure;
        }
        Credentials credentials = credentialRepository.findById(username).orElse(null);
        if (credentials == null) return failure;
        credentials.setUsername(request.getUsername());
        credentials.setPassword(request.getPassword());
        credentials.setAccountType(request.getAccountType());
        credentialRepository.save(credentials);
        return success;
    }

    @DeleteMapping(path = "/credentials/{username}")
    String deleteCredentials(@PathVariable String username){
        credentialRepository.deleteById(username);
        return success;
    }
}