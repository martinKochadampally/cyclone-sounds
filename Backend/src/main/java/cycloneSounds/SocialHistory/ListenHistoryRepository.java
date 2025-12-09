package cycloneSounds.SocialHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ListenHistoryRepository extends JpaRepository<ListenHistory, Integer>
{
    List<ListenHistory> findTop10BySongIdOrderByListenedAtDesc(String songId);
}