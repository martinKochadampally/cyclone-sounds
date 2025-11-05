package cycloneSounds.Friends;

import cycloneSounds.profilePage.Profile;
import cycloneSounds.profilePage.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
public class FriendsService {

    private final FriendsRepository friendsRepository;
    private final ProfileRepository profileRepository;

    @Autowired
    public FriendsService(FriendsRepository friendsRepository, ProfileRepository profileRepository) {
        this.friendsRepository = friendsRepository;
        this.profileRepository = profileRepository;
    }

    public Friends sendFriendRequest(String requesterUsername, String receiverUsername) {
        Profile requester = profileRepository.findByUsername(requesterUsername).orElseThrow(() -> new RuntimeException("Requester not found"));
        Profile receiver = profileRepository.findByUsername(receiverUsername).orElseThrow(() -> new RuntimeException("Receiver not found"));

        Optional<Friends> existing = friendsRepository.findFirstByRequesterAndReceiverOrReceiverAndRequester(requester, receiver, requester, receiver);

        if (existing.isPresent()) {
            throw new RuntimeException("Friend request already exists");
        }

        Friends newRequest = new Friends();
        newRequest.setRequester(requester);
        newRequest.setReceiver(receiver);
        newRequest.setStatus(Friends.Status.PENDING);
        return friendsRepository.save(newRequest);
    }

    public Friends respondToRequest(String requestId, Friends.Status status) {
        Friends request = friendsRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus(status);
        return friendsRepository.save(request);
    }

    public List<Profile> getAcceptedFriends(String username) {
        Profile user = profileRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        List<Friends> friendships = friendsRepository.findAllByRequesterAndStatusOrReceiverAndStatus(user, Friends.Status.ACCEPTED, user, Friends.Status.ACCEPTED);

        List<Profile> friends = new ArrayList<>();
        for (Friends friendship : friendships)
        {
            if (friendship.getRequester().equals(user))
            {
                friends.add(friendship.getReceiver());
            }
            else
            {
                friends.add(friendship.getRequester());
            }
        }
        return friends;
    }

    public List<Friends> getPendingRequests(String username) {
        Profile user = profileRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return friendsRepository.findByReceiverAndStatus(user, Friends.Status.PENDING);
    }
}