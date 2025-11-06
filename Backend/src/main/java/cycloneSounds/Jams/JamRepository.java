package cycloneSounds.Jams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JamRepository extends JpaRepository<Jam, String> {
    // You can add custom query methods here if needed
}