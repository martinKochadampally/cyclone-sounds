package cycloneSounds.Jams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JamMessageRepository extends JpaRepository<JamMessage, Long>{

    List<JamMessage> findByJam_NameOrderBySentAsc(String jamName);
}
