package cycloneSounds.Vote;

import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VoteService {
    @Autowired
    private UserVoteRepository userVoteRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Async
    public void recordVoteAsync(int voteSessionId, String username, String choice) {
        try {
            Vote session = voteRepository.findById(voteSessionId).orElse(null);
            if (session != null) {
                UserVote ballot = new UserVote(session, username, choice);
                userVoteRepository.save(ballot);
            }
        } catch (Exception e) {
            System.err.println("Failed to save vote: " + e.getMessage());
        }
    }
}
