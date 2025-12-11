package cycloneSounds.AlbumReview;

import cycloneSounds.Albums.Album;
import cycloneSounds.Albums.AlbumRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This is the album review controller that uses the RESTful API to allow for the creation and enable
 * operations for albumReview. This class allows users to post reviews about an album from the database.
 * @author Mark Seward
 */
@RestController
public class AlbumReviewController {

    @Autowired
    AlbumReviewRepository albumReviewRepository;

    @Autowired
    AlbumRepository albumRepository;

    /**
     * Creates a new review associated with an album from our database.
     * Records the user who reviewed it, rating, albumId value, text review and the ability to choose the
     * worst and best songs
     * @param reviewer
     * @param albumId
     * @param rating
     * @param reviewText
     * @param bestSong
     * @param worstSong
     * @return new created AlbumReview object  or null if the albumId does not exist.
     */
    @Operation(summary = "Create an album review", description = "Creates a review for an album with best/worst songs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Album review created"),
            @ApiResponse(responseCode = "404", description = "Album not found")
    })
    @PostMapping(path = "/review/album")
    public AlbumReview createAlbumReview(@RequestParam String reviewer,
                                         @RequestParam int albumId,
                                         @RequestParam int rating, // Changed to int
                                         @RequestParam String reviewText,
                                         @RequestParam(required = false) String bestSong,
                                         @RequestParam(required = false) String worstSong) {

        Album album = albumRepository.findById(albumId).orElse(null);

        if (album == null) {
            return null;
        }

        AlbumReview newReview = new AlbumReview();
        newReview.setAlbumReviewer(reviewer);
        newReview.setReviewingAnAlbum(album);

        newReview.setAlbumRating(rating);
        newReview.setAlbumReviewText(reviewText);

        newReview.setBestSong(bestSong);
        newReview.setWorstSong(worstSong);

        return albumReviewRepository.save(newReview);
    }

    /**
     * This method retrieves a list of all reviews linked to an album
     * @param albumId
     * @return list of reviewAlbum  objects
     */
    @Operation(summary = "Get reviews for a specific album")
    @GetMapping(path = "/review/album/{albumId}")
    public List<AlbumReview> getAlbumReviews(@PathVariable int albumId) {
        return albumReviewRepository.findByAlbum_AlbumId(albumId);
    }
}