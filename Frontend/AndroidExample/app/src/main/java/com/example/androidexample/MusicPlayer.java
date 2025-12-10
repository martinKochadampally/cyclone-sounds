package com.example.androidexample;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A singleton helper class to manage music playback using MediaPlayer.
 * This class handles fetching song URLs and controlling the player instance.
 */
public class MusicPlayer {

    private static MusicPlayer instance;
    private MediaPlayer mediaPlayer;
    private RequestQueue requestQueue;

    private static final String BASE_URL = "http://coms-3090-008.class.las.iastate.edu:8080";

    /**
     * Private constructor to prevent direct instantiation.
     */
    private MusicPlayer() {
        // Initialize MediaPlayer
        mediaPlayer = new MediaPlayer();
    }

    /**
     * Returns the singleton instance of the MusicPlayer.
     *
     * @return The single instance of MusicPlayer.
     */
    public static synchronized MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    /**
     * Gets the Volley RequestQueue, creating it if it doesn't exist.
     * @param context The application context.
     * @return The RequestQueue.
     */
    private RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Fetches a user's favorite song from their profile and initiates playback.
     * @param context The context of the calling Activity.
     * @param username The username of the user whose favorite song should be played.
     */
    public void playFavoriteSong(Context context, String username) {
        String profileUrl = BASE_URL + "/profiles/" + username;
        JsonObjectRequest profileRequest = new JsonObjectRequest(Request.Method.GET, profileUrl, null,
                response -> {
                    try {
                        String favSong = response.getString("favSong");
                        if (favSong != null && !favSong.isEmpty() && !favSong.equals("null")) {
                            getSongIdAndPlay(context, favSong);
                        } else {
                            Toast.makeText(context, "User has no favorite song.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Could not find favorite song.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Could not fetch profile.", Toast.LENGTH_SHORT).show());
        getRequestQueue(context).add(profileRequest);
    }

    /**
     * Searches for a song by name to get its ID, then constructs the stream URL and plays it.
     * @param context The context of the calling Activity.
     * @param songName The name of the song to search for.
     */
    public void getSongIdAndPlay(Context context, String songName) {
        String searchUrl = BASE_URL + "/search/songs/" + songName;
        JsonArrayRequest searchRequest = new JsonArrayRequest(Request.Method.GET, searchUrl, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject song = response.getJSONObject(0); // Assume the first result is the correct one
                            int songId = song.getInt("songId");
                            String streamUrl = BASE_URL + "/songs/play/" + songId;
                            playSong(context, streamUrl);
                        } else {
                            Toast.makeText(context, "Song titled '" + songName + "' not found.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(context, "Error parsing song data.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(context, "Failed to find song.", Toast.LENGTH_SHORT).show());
        getRequestQueue(context).add(searchRequest);
    }

    /**
     * Stops any current playback, sets up the MediaPlayer for a new URL, and starts playing.
     * @param context The context of the calling Activity.
     * @param url The direct streaming URL of the song.
     */
    private void playSong(Context context, String url) {
        // Stop and reset the existing player if it's playing
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(url);
            // Prepare the player asynchronously
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.prepareAsync();
            Toast.makeText(context, "Playing song...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Could not play song. Invalid URL or network issue.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Stops the music playback.
     */
    public void stop() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**
     * Releases the MediaPlayer resources. This should be called when the player
     * is no longer needed, such as in the onDestroy() method of an Activity.
     */
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        instance = null; // Allow the singleton to be garbage collected if needed
    }
}
