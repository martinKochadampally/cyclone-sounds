package coms309.music;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;


public class Playlist {

    private String name;

    private List<Song> playlist;

    public Playlist(String name){
        this.name = name;
        this.playlist = new ArrayList<>();
    }

    public String getName(){
        return name;
    }

    public List<Song> getSongs(){
        return playlist;
    }

    public void addSong(Song song){
        playlist.add(song);
    }
}
