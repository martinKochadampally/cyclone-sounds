package cycloneSounds.Jams;

import cycloneSounds.Credentials.CredentialRepository;
import cycloneSounds.Credentials.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/jams")
public class JamController {
    @Autowired
    private JamRepository jamRepository;

    @Autowired
    private CredentialRepository credentialRepository;

    // Create a new Jam
    @PostMapping("/{username}/{jamName}")
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
        jam.setManager(username);
        jam.setMembers(List.of(username)); // Optional: add only creator
        Jam savedJam = jamRepository.save(jam);
        return ResponseEntity.ok(savedJam);
    }

    // Get all Jams
    @GetMapping
    public List<Jam> getAllJams() {
        return jamRepository.findAll();
    }

    // Get a specific Jam by name
    @GetMapping("/{name}")
    public ResponseEntity<Jam> getJam(@PathVariable String name) {
        Optional<Jam> jamOpt = jamRepository.findById(name);
        return jamOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Optional: Delete a Jam
    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteJam(@PathVariable String name) {
        if (!jamRepository.existsById(name)) {
            return ResponseEntity.notFound().build();
        }
        jamRepository.deleteById(name);
        return ResponseEntity.noContent().build();
    }
}
