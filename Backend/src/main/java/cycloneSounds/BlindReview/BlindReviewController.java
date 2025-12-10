package cycloneSounds.BlindReview;

import cycloneSounds.Reviews.Review;
import cycloneSounds.Reviews.ReviewRepository;
import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import cycloneSounds.Songs.SongDTO;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blind-review")
public class BlindReviewController {
    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    // GET /blind-review/next?username=martin
    @GetMapping("/next")
    public ResponseEntity<SongDTO> getNextBlindSong(@RequestParam String username) {
        List<Song> allSongs = songRepository.findAll();

        List<Review> userReviews = reviewRepository.findByReviewer(username);
        Set<Integer> reviewedSongIds = userReviews.stream()
                .map(r -> r.getSong().getSongId())
                .collect(Collectors.toSet());

        List<Song> unreviewed = allSongs.stream()
                .filter(s -> !reviewedSongIds.contains(s.getSongId()))
                .toList();

        if (unreviewed.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        Random random = new Random();
        int index = random.nextInt(unreviewed.size());
        Song chosen = unreviewed.get(index);

        return ResponseEntity.ok(new SongDTO(chosen));
//        return songRepository.findRandomSongNotReviewedBy(username)
//                .map(song -> ResponseEntity.ok(new SongDTO(song)))
//                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    // POST /blind-review/review
    @PostMapping("/review")
    public ResponseEntity<Void> submitReview(@RequestBody BlindReview request) {
        submitBlindReview(request);
        return ResponseEntity.ok().build();
    }

    public void submitBlindReview(BlindReview req) {
        String username = req.getUsername();
        Song song = songRepository.findById(req.getSongId()).orElseThrow(() -> new IllegalArgumentException("Song not found"));

        Review review = new Review(username, req.getRating(), req.getReviewText());
        review.setSong(song);

        reviewRepository.save(review);
    }
}
