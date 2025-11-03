package cycloneSounds.Friends;

import cycloneSounds.profilePage.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, String> {

    List<Friends> findAllByRequesterAndStatusOrReceiverAndStatus(
            Profile requester, Friends.Status status1, Profile receiver, Friends.Status status2);


    Optional<Friends> findFirstByRequesterAndReceiverOrReceiverAndRequester(
            Profile requester1, Profile receiver1, Profile receiver2, Profile requester2);


    Optional<Friends> findByRequesterAndReceiverAndStatus(
            Profile requester, Profile receiver, Friends.Status status);
}