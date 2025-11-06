package cycloneSounds.Reviews;

import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ReviewController {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    SongRepository songRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping(path = "/reviews")
    public List<Review> getReviews() {
        return reviewRepository.findAll();
    }

    @GetMapping(path = "/review/username/{username}")
    public List<Review> getReviewsByReviewer(@PathVariable String username) {
        return reviewRepository.findByReviewer(username);
    }

    @GetMapping(path = "/review/song/{songId}")
    public List<Review> getReviewById(@PathVariable int songId) {
        return reviewRepository.findById(songId);
    }

    @PostMapping(path = "/review")
    public Review createReview(@RequestParam String reviewer,
                               @RequestParam String songName,
                               @RequestParam String artistName,
                               @RequestParam double rating,
                               @RequestParam String description) {

        Song song = songRepository.findBySongNameAndArtist(songName, artistName).orElse(null);

        if (song == null) {
            song = new Song(songName, artistName);
            song = songRepository.save(song);
        }

        Review newReview = new Review();
        newReview.setReviewer(reviewer);
        newReview.setRating(rating);
        newReview.setBody(description);
        newReview.setSong(song);

        return reviewRepository.save(newReview);
    }

    @PutMapping(path = "/review/upvote/{songId}")
    public Review upvote(@PathVariable Integer songId) {
        Optional<Review> optionalReview = reviewRepository.findById(songId);
        if (optionalReview.isEmpty()) {
            return null;
        }
        Review review = optionalReview.get();
        review.incrementUpVotes();
        return reviewRepository.save(review);
    }

    @PutMapping(path = "/review/downvote/{songId}")
    public Review downvote(@PathVariable Integer songId) {
        Optional<Review> optionalReview = reviewRepository.findById(songId);
        if (optionalReview.isEmpty()) {
            return null;
        }
        Review review = optionalReview.get();
        review.incrementDownVotes();
        return reviewRepository.save(review);
    }

    @DeleteMapping(path = "/review/{songId}")
    public String deleteReview(@PathVariable int songId) {
        if (!reviewRepository.existsById(songId)) {
            return failure;
        }
        reviewRepository.deleteById(songId);
        return success;
    }
}