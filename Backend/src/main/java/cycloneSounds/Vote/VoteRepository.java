package cycloneSounds.Vote;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer>
{
    Optional<Vote> findByJam_NameAndSong_SongId(String jamName, int songId);
}
