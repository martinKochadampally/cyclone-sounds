package cycloneSounds.Reviews;

import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Retrieves a list of all reviews.
     *
     * @return List of all Review objects
     */
    @Operation(summary = "Get all reviews",
            description = "Retrieves a list of all song reviews.")
    @ApiResponse(responseCode = "200", description = "List of reviews retrieved")
    @GetMapping(path = "/reviews")
    public List<Review> getReviews() {
        return reviewRepository.findAll();
    }

    /**
     * Retrieves reviews by a specific reviewer.
     *
     * @param username Username of the reviewer
     * @return List of Review objects submitted by the reviewer
     */
    @Operation(summary = "Get reviews by reviewer",
            description = "Retrieves all reviews submitted by the specified reviewer.")
    @ApiResponse(responseCode = "200", description = "Reviews retrieved")
    @GetMapping(path = "/review/username/{username}")
    public List<Review> getReviewsByReviewer(@PathVariable String username) {
        return reviewRepository.findByReviewer(username);
    }

    /**
     * Retrieves reviews for a specific song by its ID.
     *
     * @param songId ID of the song
     * @return List of Review objects for the song
     */
    @Operation(summary = "Get reviews by song ID",
            description = "Retrieves all reviews for the song identified by the given ID.")
    @ApiResponse(responseCode = "200", description = "Reviews retrieved")
    @GetMapping(path = "/review/song/{songId}")
    public List<Review> getReviewById(@PathVariable int songId) {
        return reviewRepository.findById(songId);
    }

    /**
     * Creates a new review for the specified song.
     * If the song doesn't exist, it will be created automatically.
     *
     * @param reviewer Name of the reviewer
     * @param songName Name of the song being reviewed
     * @param artistName Artist of the song
     * @param rating Rating given by the reviewer
     * @param description Text body of the review
     * @return The saved Review object
     */
    @Operation(summary = "Create a new review",
            description = "Creates a review for a song, automatically adding the song if not found.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
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

    /**
     * Upvotes the review identified by the songId.
     *
     * @param songId ID of the song whose review is upvoted
     * @return Updated Review object or null if not found
     */
    @Operation(summary = "Upvote a review",
            description = "Increments the upvote count for the review of the specified song.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review upvoted"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
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

    /**
     * Downvotes the review identified by the songId.
     *
     * @param songId ID of the song whose review is downvoted
     * @return Updated Review object or null if not found
     */
    @Operation(summary = "Downvote a review",
            description = "Increments the downvote count for the review of the specified song.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review downvoted"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
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

    /**
     * Deletes the review identified by the songId.
     *
     * @param songId ID of the review to delete
     * @return JSON message string indicating success or failure
     */
    @Operation(summary = "Delete a review",
            description = "Deletes the review associated with the specified song ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping(path = "/review/{songId}")
    public String deleteReview(@PathVariable int songId) {
        if (!reviewRepository.existsById(songId)) {
            return failure;
        }
        reviewRepository.deleteById(songId);
        return success;
    }
}