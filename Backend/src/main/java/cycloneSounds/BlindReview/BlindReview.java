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
}
