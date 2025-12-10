package cycloneSounds.Credentials;

import java.util.List;

import cycloneSounds.profilePage.Profile;
import cycloneSounds.profilePage.ProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.regex.*;


/**
 * A Controller using RESTful API to enable full CRUDL operations for
 * the Credentials table which stores login details and account type.
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


    /**
     * Verifies if the specified username and password pair are valid for login.
     *
     * @param username The user's username for login
     * @param password The user's password for login
     * @return true if credentials match and exist; otherwise false
     */
    @Operation(summary = "Verify user login by username and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Returns true if login is successful, otherwise false")
    })
    @GetMapping(path = "/login")
    Boolean verifyLogin(@RequestParam String username, @RequestParam String password) {
        Credentials credentials = credentialRepository.findById(username).orElse(null);
        return credentials != null && credentials.getPassword().equals(password);
    }

    /**
     * Retrieves a list of all credentials stored.
     * Only includes username and account type for each record.
     *
     * @return List of all credentials (DTOs with username and account type)
     */
    @Operation(summary = "Get all credentials")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of all credentials (username and account type only)")
    })
    @GetMapping(path = "/credentials")
    List<CredentialsDTO> getAllCredentials() {
        return credentialRepository.findAll().stream().map(
                credentials -> new CredentialsDTO(credentials.getUsername(), credentials.getAccountType())
        ).toList();
    }

    /**
     * Retrieves a single credential record by username.
     *
     * @param username Username to look up
     * @return CredentialsDTO for the given username or null if not found
     */
    @Operation(summary = "Get credentials by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Credentials information for the specified username"),
            @ApiResponse(responseCode = "404", description = "Credentials not found for the username")
    })
    @GetMapping(path = "/credentials/{username}")
    CredentialsDTO getCredentialsByUsername( @PathVariable String username){
         return credentialRepository.findById(username).map(
                 credential -> new CredentialsDTO(credential.getUsername(), credential.getAccountType())
         ).orElse(null);
    }

    /**
     * Creates a new credentials record with the provided username, password, and account type.
     * Also creates an empty Profile entry linked to the username.
     *
     * @param username Username for the new account
     * @param password Password for the new account
     * @param accountType Type of account (e.g., user, admin)
     * @return JSON message indicating success or failure (duplicate username)
     */
    @Operation(summary = "Create new user credentials")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully created credentials"),
            @ApiResponse(responseCode = "400", description = "Account already exists for that username")
    })
    @PostMapping(path = "/credentials")
    String createCredentials(@RequestParam String username, @RequestParam String password, @RequestParam String accountType) {
        if (credentialRepository.existsById(username)) {
            return "{\"message\":\"An account already exists for that username.\"}";
        }

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

    /**
     * Updates credentials for an existing user by username.
     * Allows updating username, password, and account type.
     *
     * @param username Username of the record to update
     * @param request The new credential data (in request body)
     * @return JSON message indicating success or failure
     */
    @Operation(summary = "Update existing credentials by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated credentials"),
            @ApiResponse(responseCode = "400", description = "Request body is missing or invalid"),
            @ApiResponse(responseCode = "404", description = "Credentials or profile not found for the username")
    })
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

    /**
     * Deletes the credentials record associated with the given username.
     *
     * @param username Username of the record to delete
     * @return JSON message indicating success
     */
    @Operation(summary = "Delete credentials by username")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully deleted credentials"),
            @ApiResponse(responseCode = "404", description = "Credentials not found for the username")
    })
    @DeleteMapping(path = "/credentials/{username}")
    String deleteCredentials(@PathVariable String username){
        credentialRepository.deleteById(username);
        return success;
    }
}