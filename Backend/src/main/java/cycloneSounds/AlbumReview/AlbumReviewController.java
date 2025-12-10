package cycloneSounds.AlbumReview;

import cycloneSounds.Reviews.Review;
import cycloneSounds.Reviews.ReviewRepository;
import cycloneSounds.Albums.Album;
import cycloneSounds.Albums.AlbumRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/album-review")
public class AlbumReviewController {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @PostMapping("/review")
    public ResponseEntity<Void> submitReview(@RequestBody AlbumReviewRequest request) {
        submitAlbumReview(request);
        return ResponseEntity.ok().build();
    }

    public void submitAlbumReview(AlbumReviewRequest req) {
        String username = req.getUsername();

        Album album = albumRepository.findById(req.getAlbumId())
                .orElseThrow(() -> new IllegalArgumentException("Album not found"));

        Review review = new Review(username, req.getAlbumRating(), req.getAlbumRequestReviewText());

        review.setAlbum(album);

        reviewRepository.save(review);
    }
}