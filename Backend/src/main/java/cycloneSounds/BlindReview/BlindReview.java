package cycloneSounds.BlindReview;

import lombok.Getter;
import lombok.Setter;

public class BlindReview {
    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private Integer songId;

    @Getter
    @Setter
    private float rating;

    @Getter
    @Setter
    private String reviewText;

    public BlindReview(String username, Integer songId, float rating, String reviewText)
    {
        this.username = username;
        this.songId = songId;
        this.rating = rating;
        this.reviewText = reviewText;
    }
}
