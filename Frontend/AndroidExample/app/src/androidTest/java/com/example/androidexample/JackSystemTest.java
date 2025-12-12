package com.example.androidexample;

import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import androidx.appcompat.widget.SearchView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class JackSystemTest {

    // =============================================================================================
    // HOME ACTIVITY
    // =============================================================================================

    @Test
    public void testHome_UIElementsVisible() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HomeActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.music_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.jams_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.profile_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.create_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.my_playlists_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.friends_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.blind_review_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.search_button)).check(matches(isDisplayed()));

            onView(withId(R.id.home_msg_txt)).check(matches(withText("Welcome")));
            onView(withId(R.id.home_username_txt)).check(matches(withText("testuser")));
        }
    }

    @Test
    public void testHome_NoUsername() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HomeActivity.class);
        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.home_msg_txt)).check(matches(withText("Cyclone Sounds")));
            onView(withId(R.id.home_username_txt)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void testHome_AllNavigationButtons() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HomeActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.my_playlists_btn)).perform(click());
            onView(isRoot()).perform(pressBack());

            onView(withId(R.id.blind_review_btn)).perform(click());
            onView(isRoot()).perform(pressBack());

            onView(withId(R.id.search_button)).perform(click());
            onView(isRoot()).perform(pressBack());

            onView(withId(R.id.music_button_btn)).perform(click());
            onView(isRoot()).perform(pressBack());

            onView(withId(R.id.profile_button_btn)).perform(click());
            onView(isRoot()).perform(pressBack());

            onView(withId(R.id.jams_button_btn)).perform(click());
            onView(isRoot()).perform(pressBack());

            onView(withId(R.id.create_button_btn)).perform(click());
            onView(isRoot()).perform(pressBack());

            onView(withId(R.id.friends_button_btn)).perform(click());
            onView(isRoot()).perform(pressBack());
        }
    }

    // =============================================================================================
    // SEARCH ACTIVITY
    // =============================================================================================

    @Test
    public void testSearch_Submit_Profile() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SearchActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        try (ActivityScenario<SearchActivity> scenario = ActivityScenario.launch(intent)) {
            setText(scenario, R.id.search_view, "Jack");
            onView(withId(R.id.search_submit_button)).perform(click());
        }
    }

    @Test
    public void testSearch_Submit_Song() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SearchActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        try (ActivityScenario<SearchActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.search_type_spinner)).perform(click());
            onData(is(instanceOf(String.class))).atPosition(1).perform(click());

            setText(scenario, R.id.search_view, "Thriller");
            onView(withId(R.id.search_submit_button)).perform(click());
        }
    }

    @Test
    public void testSearch_Submit_Album() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SearchActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        try (ActivityScenario<SearchActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.search_type_spinner)).perform(click());
            onData(is(instanceOf(String.class))).atPosition(2).perform(click());

            setText(scenario, R.id.search_view, "Bad");
            onView(withId(R.id.search_submit_button)).perform(click());
        }
    }

    @Test
    public void testSearch_Click_Song() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SearchActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        try (ActivityScenario<SearchActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.search_type_spinner)).perform(click());
            onData(is(instanceOf(String.class))).atPosition(1).perform(click());

            scenario.onActivity(activity -> {
                try {
                    Class<?> songClass = Class.forName("com.example.androidexample.SearchActivity$Song");
                    Constructor<?> constructor = songClass.getDeclaredConstructor(int.class, String.class, String.class, String.class);
                    constructor.setAccessible(true);
                    Object dummySong = constructor.newInstance(1, "Test Song", "Artist", "url");
                    ArrayList list = (ArrayList) getPrivateField(activity, "songList");
                    list.add(dummySong);
                    ((ArrayAdapter) getPrivateField(activity, "songAdapter")).notifyDataSetChanged();
                } catch (Exception e) {}
            });

            onData(anything()).inAdapterView(withId(R.id.song_results_list)).atPosition(0).perform(click());
        }
    }

    @Test
    public void testSearch_Click_Album() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SearchActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        try (ActivityScenario<SearchActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.search_type_spinner)).perform(click());
            onData(is(instanceOf(String.class))).atPosition(2).perform(click());

            scenario.onActivity(activity -> {
                try {
                    Class<?> albumClass = Class.forName("com.example.androidexample.SearchActivity$Album");
                    Constructor<?> constructor = albumClass.getDeclaredConstructor(int.class, String.class, String.class, String.class);
                    constructor.setAccessible(true);
                    Object dummyAlbum = constructor.newInstance(1, "Test Album", "Artist", "id");
                    ArrayList list = (ArrayList) getPrivateField(activity, "albumList");
                    list.add(dummyAlbum);
                    ((ArrayAdapter) getPrivateField(activity, "albumAdapter")).notifyDataSetChanged();
                } catch (Exception e) {}
            });

            onData(anything()).inAdapterView(withId(R.id.album_results_list)).atPosition(0).perform(click());
            onView(withText("View Album")).inRoot(isDialog()).perform(click());
        }
    }

    // =============================================================================================
    // FRIENDS ACTIVITY
    // =============================================================================================

    @Test
    public void testFriends_AddFriend_NetworkCall() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendsActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<FriendsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.add_friend_button)).perform(click());
            onView(withHint("Enter username")).inRoot(isDialog()).perform(replaceText("newfriend"), closeSoftKeyboard());
            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());
        }
    }

    @Test
    public void testFriends_AddFriend_Validation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendsActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<FriendsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.add_friend_button)).perform(click());
            onView(withHint("Enter username")).inRoot(isDialog()).perform(replaceText(""), closeSoftKeyboard());
            onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click());

            onView(withId(R.id.add_friend_button)).perform(click());
            onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());
        }
    }

    @Test
    public void testFriends_Dialog_AllOptions() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendsActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<FriendsActivity> scenario = ActivityScenario.launch(intent)) {
            injectFriend(scenario);

            onData(anything()).inAdapterView(withId(R.id.friends_list_view)).atPosition(0).perform(click());
            onView(withText("Send DM")).inRoot(isDialog()).perform(click());
            onView(isRoot()).perform(pressBack());

            onData(anything()).inAdapterView(withId(R.id.friends_list_view)).atPosition(0).perform(click());
            onView(withText("View Profile")).inRoot(isDialog()).perform(click());
            onView(isRoot()).perform(pressBack());

            onData(anything()).inAdapterView(withId(R.id.friends_list_view)).atPosition(0).perform(click());
            onView(withText("Play Favorite Song")).inRoot(isDialog()).perform(click());
        }
    }

    @Test
    public void testFriends_Navigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendsActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        try (ActivityScenario<FriendsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.pending_requests_button)).perform(click());
            onView(withId(R.id.home_button_btn)).perform(click());
            onView(isRoot()).perform(pressBack());
            onView(withId(R.id.profile_button_btn)).perform(click());
        }
    }

    // =============================================================================================
    // PROFILE ACTIVITY
    // =============================================================================================

    @Test
    public void testProfile_Edit() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("PROFILE_TO_VIEW", "testuser");
        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.profile_update_btn)).perform(click());
            onView(withId(R.id.profile_delete_btn)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testProfile_ReadOnly() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("PROFILE_TO_VIEW", "otheruser");
        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.profile_update_btn)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void testProfile_Actions() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("PROFILE_TO_VIEW", "testuser");
        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.profile_logout_btn)).perform(click());
        }
    }

    // =============================================================================================
    // ALBUM ACTIVITY
    // =============================================================================================

    @Test
    public void testAlbum_ListClick_WithUrl() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AlbumActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("ALBUM_NAME", "Dummy");
        intent.putExtra("ALBUM_ID", 999);
        try (ActivityScenario<AlbumActivity> scenario = ActivityScenario.launch(intent)) {
            injectSongs(scenario);
            onData(anything()).inAdapterView(withId(R.id.album_songs_list)).atPosition(0).perform(click());
            onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click());
        }
    }

    @Test
    public void testAlbum_Navigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AlbumActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("ALBUM_ID", 1);
        try (ActivityScenario<AlbumActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.profile_button_btn)).perform(click());
        }
    }

    // =============================================================================================
    // BLIND REVIEW ACTIVITY
    // =============================================================================================

    @Test
    public void testBlindReview_Validation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BlindReviewActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        try (ActivityScenario<BlindReviewActivity> scenario = ActivityScenario.launch(intent)) {
            scenario.onActivity(activity -> setPrivateField(activity, "currentEmbedUrl", ""));
            onView(withId(R.id.blind_play_btn)).perform(click());

            scenario.onActivity(activity -> setPrivateField(activity, "currentSongId", -1));
            onView(withId(R.id.submit_blind_review_btn)).perform(click());
        }
    }

    // =============================================================================================
    // ALBUM REVIEW ACTIVITY
    // =============================================================================================

    @Test
    public void testAlbumReview_Submission() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AlbumReviewActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("ALBUM_ID", 100);
        intent.putExtra("ALBUM_NAME", "Review Album");
        try (ActivityScenario<AlbumReviewActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.best_song_input)).perform(replaceText("Thriller"), closeSoftKeyboard());
            onView(withId(R.id.submit_review_btn)).perform(click());
        }
    }

    // =============================================================================================
    // LOGIN ACTIVITY
    // =============================================================================================

    @Test
    public void testLoginFlow() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(intent)) {
            setText(scenario, R.id.login_username_edt, "user");
            setText(scenario, R.id.login_password_edt, "pass");
            onView(withId(R.id.login_login_btn)).perform(click());
        }
    }

    // =============================================================================================
    // DM ACTIVITY
    // =============================================================================================

    @Test
    public void testDMSending() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DMActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "u1");
        intent.putExtra("FRIEND_USERNAME", "u2");
        try (ActivityScenario<DMActivity> scenario = ActivityScenario.launch(intent)) {
            setText(scenario, R.id.message_input, "Hi");
            onView(withId(R.id.send_button)).perform(click());
        }
    }

    // =============================================================================================
    // HELPER METHODS
    // =============================================================================================

    private void setText(ActivityScenario<?> scenario, int viewId, String text) {
        scenario.onActivity(activity -> {
            try {
                android.view.View view = activity.findViewById(viewId);
                if (view instanceof EditText) {
                    ((EditText) view).setText(text);
                } else if (view instanceof SearchView) {
                    ((SearchView) view).setQuery(text, false);
                }
            } catch (Exception e) {}
        });
    }

    private void injectFriend(ActivityScenario<FriendsActivity> scenario) {
        scenario.onActivity(activity -> {
            try {
                ArrayList<String> list = (ArrayList<String>) getPrivateField(activity, "friendsList");
                list.add("TestFriend");
                ((ArrayAdapter) getPrivateField(activity, "friendsAdapter")).notifyDataSetChanged();
            } catch (Exception e) {}
        });
    }

    private void injectSongs(ActivityScenario<AlbumActivity> scenario) {
        scenario.onActivity(activity -> {
            try {
                ArrayList<String> sList = (ArrayList<String>) getPrivateField(activity, "songList");
                ArrayList<String> uList = (ArrayList<String>) getPrivateField(activity, "songUrls");
                sList.add("Song"); uList.add("http://url.com");
                ((ArrayAdapter) getPrivateField(activity, "songsAdapter")).notifyDataSetChanged();
            } catch (Exception e) {}
        });
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private Object getPrivateField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}