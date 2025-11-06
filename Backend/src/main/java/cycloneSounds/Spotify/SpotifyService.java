package cycloneSounds.Spotify;

import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import cycloneSounds.Spotify.SpotifyDTO.SpotifyArtist;
import cycloneSounds.Spotify.SpotifyDTO.SpotifySearchResponse;
import cycloneSounds.Spotify.SpotifyDTO.SpotifyTrack;
import cycloneSounds.Spotify.SpotifyTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;


@Service
public class SpotifyService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;

    @Autowired
    private SongRepository songRepository;

    public SpotifyService(@Value("${spotify.client.id}") String clientId,
                          @Value("${spotify.client.secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .build();
    }

    public Mono<String> getAccessToken() {
        WebClient authClient = WebClient.builder()
                .baseUrl("https://accounts.spotify.com/api/token")
                .build();

        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        return authClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(SpotifyTokenResponse.class)
                .map(SpotifyTokenResponse::access_token);
    }

    public Mono<Void> searchAndSaveTracks(String query) {
        return getAccessToken().flatMap(token -> {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query)
                            .queryParam("type", "track")
                            .queryParam("limit", 20)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(SpotifySearchResponse.class)
                    .doOnNext(response -> {
                        if (response != null && response.getTracks() != null) {
                            saveTracksToDatabase(response.getTracks().getItems());
                        }
                    });
        }).then();
    }

    private void saveTracksToDatabase(List<SpotifyTrack> spotifyTracks) {
        if (spotifyTracks == null) {
            return;
        }

        List<Song> songsToSave = new ArrayList<>();

        for (SpotifyTrack track : spotifyTracks) {
            if (songRepository.findBySpotifyId(track.getId()).isEmpty()) {
                Song newSong = new Song();
                newSong.setSongName(track.getName());
                newSong.setSpotifyId(track.getId());

                String artistNames = track.getArtists().stream().map(SpotifyArtist::getName).collect(Collectors.joining(", "));

                if (artistNames.isEmpty()) {
                    artistNames = "Unknown Artist";
                }
                newSong.setArtist(artistNames);
                newSong.setSearches(0);

                songsToSave.add(newSong);
            }
        }

        if (!songsToSave.isEmpty()) {
            songRepository.saveAll(songsToSave);
        }
    }
}