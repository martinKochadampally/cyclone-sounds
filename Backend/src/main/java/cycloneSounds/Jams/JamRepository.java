package cycloneSounds.Jams;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JamRepository extends JpaRepository<Jam, String> {
    @Query("SELECT j FROM Jam j LEFT JOIN FETCH j.members WHERE j.name = :name")
    Optional<Jam> findByIdWithMembers(@Param("name") String name);
}