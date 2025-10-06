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
    Boolean verifyLogin(@RequestBody String username, @RequestBody String password) {
        Credentials credentials = credentialRepository.findByUsernameOrEmailId(username, username).orElse(null);
        return credentials != null && credentials.getPassword().equals(password);
    }

    @GetMapping(path = "/credentials")
    List<CredentialsDTO> getAllCredentials() {
        return credentialRepository.findAll().stream().map(
                credentials -> new CredentialsDTO(credentials.getEmailId(), credentials.getUsername(), credentials.getAccountType())
        ).toList();
    }

    @GetMapping(path = "/credentials/{emailId}")
    CredentialsDTO getCredentialsByEmail( @PathVariable String emailId){
         return credentialRepository.findById(emailId).map(
                 credential -> new CredentialsDTO(credential.getEmailId(), credential.getUsername(), credential.getAccountType())
         ).orElse(null);
    }

    @PostMapping(path = "/credentials")
    String createCredentials(@RequestParam String emailId, @RequestParam String username, @RequestParam String password, @RequestParam String accountType) {

        // First, check if the user or email already exists
        boolean emailExists = credentialRepository.existsById(emailId);
        boolean usernameExists = credentialRepository.existsByUsername(username);
        if (emailExists || usernameExists) {
            return failure;
        }

        // If not, create a new Credentials object and save it
        Credentials credential = new Credentials();
        credential.setEmailId(emailId);
        credential.setUsername(username);
        credential.setPassword(password);
        credential.setAccountType(accountType);

        credentialRepository.save(credential);
        return success;
    }

    @PutMapping(path = "/credentials/{emailId}")
    String updateCredentials(@PathVariable String emailId, @RequestBody Credentials request) {
        if (request == null || request.getEmailId() == null || request.getUsername() == null ||
                request.getPassword() == null || request.getAccountType() == null) {
            return failure;
        } else if (!credentialRepository.existsById(emailId)) {
            return failure;
        }
        Credentials credentials = credentialRepository.findById(emailId).orElse(null);
        if (credentials == null) return failure;
        credentials.setEmailId(request.getEmailId());
        credentials.setUsername(request.getUsername());
        credentials.setPassword(request.getPassword());
        credentials.setAccountType(request.getAccountType());
        credentialRepository.save(credentials);
        return success;
    }

    @DeleteMapping(path = "/credentials/{emailId}")
    String deleteCredentials(@PathVariable String emailId){
        credentialRepository.deleteById(emailId);
        return success;
    }
}