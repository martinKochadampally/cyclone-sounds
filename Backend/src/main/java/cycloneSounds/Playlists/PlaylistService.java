package cycloneSounds.Playlists;

import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private SongRepository songRepository;

    /**
     * ALlows playlists to be created, needs to have a unique name no other playlist has
     * @param playlistName
     * @param username
     * @return
     */
    @Transactional
    public Playlist createPlaylist(String playlistName, String username) {
        if (playlistRepository.findById(playlistName).isPresent())
        {
            throw new RuntimeException("Playlist with this name already exists");
        }

        Playlist playlist = new Playlist();
        playlist.setPlaylistName(playlistName);
        playlist.setUsername(username);

        return playlistRepository.save(playlist);
    }

    /**
     * Allows playlists to be deleted
     * @param username
     * @param playlistName
     */
    @Transactional
    public void deletePlaylist(String username, String playlistName) {
        Playlist playlist = playlistRepository.findById(playlistName)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        playlistRepository.delete(playlist);

    }

    /**
     * Allows songs to be added to playlists. Search to make sure playlist and songs exist in the databases
     * @param username
     * @param playlistName
     * @param songName
     * @param artist
     * @return
     */
    @Transactional
    public Playlist addSongToPlaylist(String username, String playlistName, String songName, String artist) {
        Playlist playlist = playlistRepository.findById(playlistName)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        Song song = songRepository.findBySongNameAndArtist(songName, artist)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        playlist.addSong(song);
        return playlistRepository.save(playlist);
    }

    /**
     * Allows playlists to be deleted by anyone. and checks if it actually exists
     * @param username
     * @param playlistName
     * @param songName
     * @param artist
     * @return
     */
    @Transactional
    public Playlist removeSongFromPlaylist(String username, String playlistName, String songName, String artist) {
        Playlist playlist = playlistRepository.findById(playlistName)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        Set<Song> songs = playlist.getSongs();
        if (songs.isEmpty()) {
            throw new RuntimeException("Playlist is empty");
        }
        Song songToRemove = songs.stream()
                .filter(song -> song.getSongName().equals(songName) && song.getArtist().equals(artist))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Song not found in this playlist"));

        playlist.removeSong(songToRemove);
        return playlistRepository.save(playlist);
    }

    /**
     * FInds all playlists that a user is a part of
     * @param username
     * @return
     */
    @Transactional(readOnly = true)
    public List<Playlist> getPlaylistsByOwner(String username) {

        return playlistRepository.findByUsername(username);
    }

    /**
     * Searches for all songs in a playlist, if playlist exists
     * @param username
     * @param playlistName
     * @return
     */
    @Transactional(readOnly = true)
    public Set<Song> getSongsFromPlaylist(String username, String playlistName) {
        Playlist playlist = playlistRepository.findById(playlistName)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        return playlist.getSongs();
    }
}