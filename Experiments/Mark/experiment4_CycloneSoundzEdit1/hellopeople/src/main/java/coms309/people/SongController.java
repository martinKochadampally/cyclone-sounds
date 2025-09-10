package coms309.people;

import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class SongController {

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.
    HashMap<String, Song> songList = new  HashMap<>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input. 
    // Springboot automatically converts the list to JSON format 
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @GetMapping("/songs")
    public  HashMap<String, Song> getAllSongs() {
        return songList;
    }

    // THIS IS THE CREATE OPERATION
    // springboot automatically converts JSON input into a person object and 
    // the method below enters it into the list.
    // It returns a string message in THIS example.
    // Note: To CREATE we use POST method
    @PostMapping("/song")
    public  String createSong(@RequestBody Song song) {
        System.out.println(song);
        songList.put(song.getTitle(), song);
        String s = "New song "+ song.getTitle() + " Saved";
        return s;
        //public  ResponseEntity<Map<String, String>>  //unused
        // createPerson(@RequestBody Person person) { // unused
        //Map <String, String> body = new HashMap<>();// unused
        //body.put("message", s); // unused
        //ResponseEntity<>(body, HttpStatus.OK); // unused
    }

    // THIS IS THE READ OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We extract the person from the HashMap.
    // springboot automatically converts Person to JSON format when we return it
    // Note: To READ we use GET method
    @GetMapping("/song/{title}")
    public Song getSong(@PathVariable String title) {
        Song s = songList.get(title);
        return s;
    }

    // THIS IS A GET METHOD
    // RequestParam is expected from the request under the key "name"
    // returns all names that contains value passed to the key "name"
    @GetMapping("/song/contains")
    public List<Song> getSongByParam(@RequestParam("title") String name) {
        List<Song> res = new ArrayList<>();
        for (Song s : songList.values()) {
            if (s.getTitle().contains(name))
                res.add(s);
        }
        return res;
    }

    // THIS IS THE UPDATE OPERATION
    // We extract the person from the HashMap and modify it.
    // Springboot automatically converts the Person to JSON format
    // Springboot gets the PATHVARIABLE from the URL
    // Here we are returning what we sent to the method
    // Note: To UPDATE we use PUT method
    @PutMapping("/song/{song}")
    public Song updateSong(@PathVariable String song, @RequestBody Song s) {
        songList.replace(song, s);
        return songList.get(song);
    }


    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // Note: To DELETE we use delete method
    
    @DeleteMapping("/songs/{title}")
    public HashMap<String, Song> deleteSong(@PathVariable("title") String song) {
        songList.remove(song);
        return songList;
    }

    //Search songs by artist
    @GetMapping("/songs/search/artist")
    public List<Song> getSongByArtist(@RequestParam("artist") String artist){
        List<Song> res = new ArrayList<>();
        for(Song s : songList.values()){
            if(s.getArtist().equals(artist))
            {
                res.add(s);
            }
        }
        return res;
    }

    //Get method
    //Counts the number of songs added to the HashMa
    @GetMapping("/songs/count")
    public int getSongCount(){
        return songList.size();
    }
} // end of people controller

