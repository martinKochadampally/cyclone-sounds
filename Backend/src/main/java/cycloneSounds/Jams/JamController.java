package cycloneSounds.Jams;

import cycloneSounds.Credentials.CredentialRepository;
import cycloneSounds.Credentials.Credentials;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Rest Controller Allowing Creating Reading and Deleteing Jams from the
 * database.
 *
 * @author martin
 */
@RestController
@RequestMapping("/api/jams")
public class JamController {
    @Autowired
    private JamRepository jamRepository;

    @Autowired
    private CredentialRepository credentialRepository;

    /**
     * Creates a new Jam with the specified name and assigns the user as manager.
     * Only users with 'jamManager' or 'admin' account types are authorized.
     *
     * @param username Username of the jam manager creating the Jam
     * @param jamName Name of the Jam to create
     * @return ResponseEntity containing the created Jam or error status
     */
    @Operation(summary = "Create a new Jam",
            description = "Creates a new Jam if the user is authorized and the Jam name is not already taken.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jam successfully created"),
            @ApiResponse(responseCode = "400", description = "Jam name already exists"),
            @ApiResponse(responseCode = "403", description = "User not authorized to create Jam")
    })
    @PostMapping("/{username}/{jamName}/{approvalType}")
    public ResponseEntity<Jam> createJam(@PathVariable String username, @PathVariable String jamName) {
        Credentials creds = credentialRepository.findById(username).orElse(null);
        if (creds == null || (!creds.getAccountType().equals("jamManager") && !creds.getAccountType().equals("admin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (jamRepository.existsById(jamName)) {
            return ResponseEntity.badRequest().body(null);
        }
        Jam jam = new Jam();
        jam.setName(jamName);
        jam.setApprovalType(approvalType);
        jam.setManager(username);
        jam.setMembers(List.of(username));
        Jam savedJam = jamRepository.save(jam);
        return ResponseEntity.ok(savedJam);
    }

    /**
     * Retrieves all Jams from the repository.
     *
     * @return List of all Jam entities.
     */
    @Operation(summary = "Get all Jams",
            description = "Retrieves a list of all Jams.")
    @ApiResponse(responseCode = "200", description = "List of Jams successfully retrieved")
    @GetMapping
    public List<Jam> getAllJams() {
        return jamRepository.findAll();
    }

    /**
     * Retrieves a Jam by its name.
     *
     * @param name Name of the Jam to retrieve
     * @return ResponseEntity containing the Jam if found, or 404 if not found
     */
    @Operation(summary = "Get Jam by name",
            description = "Retrieves details about a specific Jam by its name.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jam found"),
            @ApiResponse(responseCode = "404", description = "Jam not found")
    })
    @GetMapping("/{name}")
    public ResponseEntity<Jam> getJam(@PathVariable String name) {
        Optional<Jam> jamOpt = jamRepository.findById(name);
        return jamOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a Jam by its name.
     *
     * @param name Name of the Jam to delete
     * @return ResponseEntity with no content on successful deletion, or 404 if not found
     */
    @Operation(summary = "Delete Jam by name",
            description = "Deletes the Jam identified by the specified name.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Jam successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Jam not found")
    })
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteJam(@PathVariable String name) {
        if (!jamRepository.existsById(name)) {
            return ResponseEntity.notFound().build();
        }
        jamRepository.deleteById(name);
        return ResponseEntity.noContent().build();
    }
}
