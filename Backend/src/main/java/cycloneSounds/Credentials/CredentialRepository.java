package cycloneSounds.Credentials;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 *
 * @author Martin Kochadampally
 *
 */
public interface CredentialRepository extends JpaRepository<Credentials, String> {

}
