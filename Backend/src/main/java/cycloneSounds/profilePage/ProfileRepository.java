package cycloneSounds.profilePage;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 
 * @author Vivek Bengre
 * 
 */
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    List<Profile> findByUsernameContainingIgnoreCaseAndUsernameNot(String query, String myUsername);
}
