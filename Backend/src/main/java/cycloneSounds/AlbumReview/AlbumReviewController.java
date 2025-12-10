package cycloneSounds.AlbumReview;

import cycloneSounds.Albums.Album;
import cycloneSounds.Albums.AlbumRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AlbumReviewController {

    @Autowired
    AlbumReviewRepository albumReviewRepository;

    @Autowired
    AlbumRepository albumRepository;

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

    @Operation(summary = "Get reviews for a specific album")
    @GetMapping(path = "/review/album/{albumId}")
    public List<AlbumReview> getAlbumReviews(@PathVariable int albumId) {
        return albumReviewRepository.findByAlbum_AlbumId(albumId);
    }
}