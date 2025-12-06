package cycloneSounds.Spotify;

import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import cycloneSounds.Spotify.SpotifyDTO.SpotifyArtist;
import cycloneSounds.Spotify.SpotifyDTO.SpotifySearchResponse;
import cycloneSounds.Spotify.SpotifyDTO.SpotifyTrack;
import cycloneSounds.Spotify.SpotifyDTO.SpotifyTracksResponse;
import cycloneSounds.Albums.Album;
import cycloneSounds.Albums.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service class for interacting with the Spotify API.
 * Handles token retrieval, track searching and database persistence.
 */
@Service
public class SpotifyService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private AlbumRepository albumRepository;
    /**
     * Constructor initializes Spotify service with credentials.
     * @param clientId
     * @param clientSecret
     */
    public SpotifyService(@Value("${spotify.client.id}") String clientId,
                          @Value("${spotify.client.secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.spotify.com/v1")
                .build();
    }

    /**
     * Retrieves an access token from Spotify using client credentials.
     * @return mono
     */
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

    /**
     * Searches for tracks on Spotify using a query and saves new tracks to the database
     * @param query
     * @return
     */
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

    /**
     * Saves new tracks from Spotify to the local database.
     * Ensures no duplicate entries exist by checking Spotify ID.
     * @param spotifyTracks
     */
    private void saveTracksToDatabase(List<SpotifyTrack> spotifyTracks) {
        if (spotifyTracks == null) return;

        for (SpotifyTrack track : spotifyTracks) {
            try {
                String artistNames = track.getArtists().stream()
                        .map(SpotifyArtist::getName)
                        .collect(Collectors.joining(", "));
                if (artistNames.isEmpty()) artistNames = "Unknown Artist";

                Album album = null;
                if (track.getAlbum() != null) {
                    String albumId = track.getAlbum().getId();
                    album = albumRepository.findBySpotifyId(albumId);

                    if (album == null) {
                        album = new Album();
                        album.setTitle(track.getAlbum().getName());
                        album.setArtist(artistNames);
                        album.setSpotifyId(albumId);
                        album = albumRepository.save(album);
                    }
                }

                Song song = songRepository.findBySpotifyId(track.getId()).orElse(null);

                if (song == null) {
                    song = new Song();
                    song.setSongName(track.getName());
                    song.setSpotifyId(track.getId());
                    song.setArtist(artistNames);
                    song.setSearches(0);
                }

                if (song.getAlbum() == null && album != null) {
                    song.setAlbum(album);
                    songRepository.save(song);
                }

            } catch (Exception e) {
                System.err.println("Error saving track " + track.getName() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Searches for an Album by name, then fetches its tracks.
     */
    public Mono<Album> searchAndSaveAlbum(String albumName) {
        return getAccessToken().flatMap(token -> {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", albumName)
                            .queryParam("type", "album") // Explicitly search for ALBUMS
                            .queryParam("limit", 1)
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(SpotifySearchResponse.class)
                    .flatMap(response -> {

                        if (response == null || response.getAlbums() == null || response.getAlbums().getItems().isEmpty()) {
                            return Mono.empty();
                        }

                        var spotifyAlbum = response.getAlbums().getItems().get(0);


                        return getAlbumTracks(spotifyAlbum.getId(), token, spotifyAlbum);
                    });
        });
    }

    /**
     * Fetches the list of tracks for a specific Album ID.
     */
    private Mono<Album> getAlbumTracks(String spotifyAlbumId, String token, cycloneSounds.Spotify.SpotifyDTO.SpotifyAlbum spotifyAlbumData) {
        return webClient.get()
                .uri("/albums/" + spotifyAlbumId + "/tracks?limit=50")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(SpotifyTracksResponse.class)
                .map(trackResponse -> {
                    return saveFullAlbumToDatabase(spotifyAlbumData, trackResponse.getItems());
                });
    }

    /**
     * Saves the Album and links all its tracks.
     */
    private Album saveFullAlbumToDatabase(cycloneSounds.Spotify.SpotifyDTO.SpotifyAlbum spotifyAlbumDTO, List<SpotifyTrack> tracks) {
        Album album = albumRepository.findBySpotifyId(spotifyAlbumDTO.getId());

        if (album == null) {
            album = new Album();
            album.setTitle(spotifyAlbumDTO.getName());
            album.setSpotifyId(spotifyAlbumDTO.getId());

            if (tracks != null && !tracks.isEmpty() && !tracks.get(0).getArtists().isEmpty()) {
                album.setArtist(tracks.get(0).getArtists().get(0).getName());
            } else {
                album.setArtist("Unknown Artist");
            }
            album = albumRepository.save(album);
        }

        if (tracks != null) {
            for (SpotifyTrack track : tracks) {
                Song song = songRepository.findBySpotifyId(track.getId()).orElse(new Song());

                song.setSongName(track.getName());
                song.setSpotifyId(track.getId());


                if (!track.getArtists().isEmpty()) {
                    song.setArtist(track.getArtists().get(0).getName());
                } else {
                    song.setArtist(album.getArtist());
                }

                song.setAlbum(album);
                songRepository.save(song);
            }
        }
        return album;
    }
}