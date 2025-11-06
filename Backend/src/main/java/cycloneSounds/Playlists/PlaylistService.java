package cycloneSounds.Playlists;

import cycloneSounds.Songs.Song;
import cycloneSounds.Songs.SongRepository;
import cycloneSounds.profilePage.Profile;
import cycloneSounds.profilePage.ProfileRepository;
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
    private ProfileRepository profileRepository;

    @Autowired
    private SongRepository songRepository;

    @Transactional
    public Playlist createPlaylist(String playlistName, String ownerUsername) {
        cycloneSounds.profilePage.Profile owner = profileRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        if (playlistRepository.findById(playlistName).isPresent())
        {
            throw new RuntimeException("Playlist with this name already exists");
        }

        Playlist playlist = new Playlist();
        playlist.setPlaylistName(playlistName);
        playlist.setOwner(owner);

        return playlistRepository.save(playlist);
    }

    @Transactional
    public void deletePlaylist(String playlistName) {
        Playlist playlist = playlistRepository.findById(playlistName)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
        playlistRepository.delete(playlist);
    }

    @Transactional
    public Playlist addSongToPlaylist(String playlistName, int songId) {
        Playlist playlist = playlistRepository.findById(playlistName)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        playlist.addSong(song);
        return playlistRepository.save(playlist);
    }

    @Transactional
    public Playlist removeSongFromPlaylist(String playlistName, int songId) {
        Playlist playlist = playlistRepository.findById(playlistName)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        playlist.removeSong(song);
        return playlistRepository.save(playlist);
    }

    @Transactional(readOnly = true)
    public List<Playlist> getPlaylistsByOwner(String ownerUsername) {
        cycloneSounds.profilePage.Profile owner = profileRepository.findByUsername(ownerUsername)
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        return playlistRepository.findByOwner(owner);
    }

    @Transactional(readOnly = true)
    public Set<Song> getSongsFromPlaylist(String playlistName) {
        Playlist playlist = playlistRepository.findById(playlistName)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        return playlist.getSongs();
    }
}