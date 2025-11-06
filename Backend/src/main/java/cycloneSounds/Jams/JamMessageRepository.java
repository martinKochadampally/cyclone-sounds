package cycloneSounds.Jams;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JamMessageRepository extends JpaRepository<JamMessage, Long>{

    List<JamMessage> findByJam_NameOrderBySentAsc(String jamName);
}
