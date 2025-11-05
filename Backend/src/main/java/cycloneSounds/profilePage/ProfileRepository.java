package cycloneSounds.profilePage;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * The JPA repository for Profile.
 *
 * @author Martin Kochadampally
 * 
 */
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    List<Profile> findByUsernameContainingIgnoreCaseAndUsernameNot(String query, String myUsername);
    List<Profile> findTop10ByUsernameContainingOrderByViewsDesc(String searchKey);
    Optional<Profile> findByUsername(String username);
}
