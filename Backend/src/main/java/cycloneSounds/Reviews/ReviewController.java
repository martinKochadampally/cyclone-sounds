package cycloneSounds.Reviews;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Set;

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
    public List<Review> getReviewsByReviewer(@PathVariable String username){
        return reviewRepository.findByReviewer(username);
    }

    @GetMapping(path = "/review/song/{songId}")
    public List<Review> getReviewById(@PathVariable int songId){
        return reviewRepository.findById(songId);
    }
    /**
    @GetMapping(path = "/review/artist/{artist}")
    public List<Review> getReviewbyArtist(@PathVariable String artist){
        return reviewRepository.findByArtist(artist);
    }*/

    //Need this from front end: reviewer, rating, body, songName, and artist.
    @PostMapping(path = "/review")
    public Review createReview(@RequestParam String songName, @RequestParam String artistName, @RequestParam String  reviewer, @RequestParam String description, @RequestParam double rating) {
        // Find the song by its name and artist only
        Song song = songRepository.findBySongNameAndArtist(songName, artistName).orElse(null);

        // If the song doesn't exist, create a new one
        if (song == null) {
            song = new Song(songName, artistName);
            song = songRepository.save(song);
        }

        // Create the new review
        Review newReview = new Review();
        newReview.setReviewer(reviewer);
        newReview.setRating(rating);
        newReview.setBody(description); // Assumes your Review entity has a setBody() method
        newReview.setSong(song);

        return reviewRepository.save(newReview);
    }

    @PutMapping(path = "/review/upvote/{songId}")
    public Review upvote(@PathVariable Integer songId){
        Optional<Review> optionalReview = reviewRepository.findById(songId);
        if(optionalReview.isEmpty()) {
            return null;
        }
            Review review = optionalReview.get();
            review.incrementUpVotes();
            return reviewRepository.save(review);
            //return review;
        }

    @PutMapping(path = "/review/downvote/{songId}")
    public Review downvote(@PathVariable Integer songId){
        Optional<Review> optionalReview = reviewRepository.findById(songId);
        if(optionalReview.isEmpty()) {
            return null;
        }
            Review review = optionalReview.get();
            review.incrementDownVotes();
            return reviewRepository.save(review);
    }

    @DeleteMapping(path = "/review/{songId}")
    public String deleteReview(@PathVariable int songId)
    {
        if(!reviewRepository.existsById(songId)) {
            return failure;
        }
        reviewRepository.deleteById(songId);
        return success;
    }

    /**
     * Finds all reviews for a specific song using the artist and song name.
     */
    @GetMapping("/review/by-song")
    public ResponseEntity<List<Review>> getReviewsBySongDetails(
            @RequestParam String reviewer,@RequestParam String songName,
            @RequestParam String artist, @RequestParam double rating, @RequestParam String body) {

        Optional<Song> optionalSong = songRepository.findBySongNameAndArtist(songName, artist);

        if (optionalSong.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        int songId = optionalSong.get().getSongId();
        List<Review> reviews = reviewRepository.findBySong_SongId(songId);
        return ResponseEntity.ok(reviews);
    }
}
