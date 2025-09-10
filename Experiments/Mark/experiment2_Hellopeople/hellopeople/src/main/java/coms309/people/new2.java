package coms309.songs;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class SongController {

    HashMap<String, Song> songList = new HashMap<>();

    // LIST all songs
    @GetMapping("/songs")
    public HashMap<String, Song> getAllSongs() {
        return songList;
    }

    // CREATE a new song
    @PostMapping("/songs")
    public String createSong(@RequestBody Song song) {
        songList.put(song.getTitle(), song);
        return "New song \"" + song.getTitle() + "\" saved!";
    }

    // READ a song by title
    @GetMapping("/songs/{title}")
    public Song getSong(@PathVariable String title) {
        return songList.get(title);
    }

    // UPDATE a song
    @PutMapping("/songs/{title}")
    public Song updateSong(@PathVariable String title, @RequestBody Song s) {
        songList.replace(title, s);
        return songList.get(title);
    }

    // DELETE a song
    @DeleteMapping("/songs/{title}")
    public HashMap<String, Song> deleteSong(@PathVariable String title) {
        songList.remove(title);
        return songList;
    }

    // EXTRA: Search songs by artist
    @GetMapping("/songs/search/artist")
    public List<Song> getSongsByArtist(@RequestParam("artist") String artist) {
        List<Song> res = new ArrayList<>();
        for (Song s : songList.values()) {
            if (s.getArtist().equalsIgnoreCase(artist)) {
                res.add(s);
            }
        }
        return res;
    }

    // EXTRA: Search songs by genre
    @GetMapping("/songs/search/genre")
    public List<Song> getSongsByGenre(@RequestParam("genre") String genre) {
        List<Song> res = new ArrayList<>();
        for (Song s : songList.values()) {
            if (s.getGenre().equalsIgnoreCase(genre)) {
                res.add(s);
            }
        }
        return res;
    }

    // EXTRA: Count songs
    @GetMapping("/songs/count")
    public int getSongCount() {
        return songList.size();
    }
}
