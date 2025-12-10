package com.example.androidexample;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class JackSystemTest {

    // ==========================================
    // 1. LoginActivity Tests
    // ==========================================
    @Test
    public void testLoginFlow() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(intent)) {
            // Test typing in fields
            onView(withId(R.id.login_username_edt)).perform(typeText("testuser"), closeSoftKeyboard());
            onView(withId(R.id.login_password_edt)).perform(typeText("password123"), closeSoftKeyboard());

            // Check button visibility and click
            onView(withId(R.id.login_login_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.login_login_btn)).perform(click());
        }
    }

    @Test
    public void testSignupNavigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.login_signup_btn)).perform(click());
        }
    }

    // ==========================================
    // 2. HomeActivity Tests
    // ==========================================
    @Test
    public void testHomeNavigationButtons() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HomeActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify all main buttons are displayed
            onView(withId(R.id.music_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.jams_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.create_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.profile_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.my_playlists_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.friends_button_btn)).check(matches(isDisplayed()));

            // Click one to test navigation logic
            onView(withId(R.id.profile_button_btn)).perform(click());
        }
    }

    // ==========================================
    // 3. ProfileActivity Tests
    // ==========================================
    @Test
    public void testProfileUpdateFlow() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("PROFILE_TO_VIEW", "testuser"); // Own profile (Editable)

        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Check fields are displayed
            onView(withId(R.id.profile_name_edt)).check(matches(isDisplayed()));

            // Test updating data
            onView(withId(R.id.profile_bio_edt)).perform(clearText(), typeText("Updated Bio"), closeSoftKeyboard());
            onView(withId(R.id.profile_update_btn)).check(matches(isEnabled()));
            onView(withId(R.id.profile_update_btn)).perform(click());
        }
    }

    @Test
    public void testProfileReadOnly() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("PROFILE_TO_VIEW", "otheruser"); // Other profile (Read Only)

        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {
            // Ensure update button is NOT displayed for other users
            onView(withId(R.id.profile_name_edt)).check(matches(not(isEnabled())));
        }
    }

    // ==========================================
    // 4. SearchActivity Tests
    // ==========================================
    @Test
    public void testSearchFunctionality() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SearchActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<SearchActivity> scenario = ActivityScenario.launch(intent)) {
            // Check UI elements
            onView(withId(R.id.search_type_spinner)).check(matches(isDisplayed()));
            onView(withId(R.id.search_submit_button)).check(matches(isDisplayed()));

            // Perform a click on submit to trigger search logic
            onView(withId(R.id.search_submit_button)).perform(click());

            // Verify lists are present
            onView(withId(R.id.profile_results_list)).check(matches(isDisplayed()));
        }
    }

    // ==========================================
    // 5. FriendsActivity Tests
    // ==========================================
    @Test
    public void testFriendsUI() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendsActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<FriendsActivity> scenario = ActivityScenario.launch(intent)) {
            // Check List and Buttons
            onView(withId(R.id.friends_list_view)).check(matches(isDisplayed()));
            onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()));
            onView(withId(R.id.pending_requests_button)).perform(click());
        }
    }

    // ==========================================
    // 6. DMActivity Tests (Chat)
    // ==========================================
    @Test
    public void testDMSending() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DMActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "user1");
        intent.putExtra("FRIEND_USERNAME", "user2");

        try (ActivityScenario<DMActivity> scenario = ActivityScenario.launch(intent)) {
            // Check Recycler View
            onView(withId(R.id.chat_recycler_view)).check(matches(isDisplayed()));

            // Type message and send
            onView(withId(R.id.message_input)).perform(typeText("Hello World"), closeSoftKeyboard());
            onView(withId(R.id.send_button)).perform(click());
        }
    }

    // ==========================================
    // 7. AlbumActivity Tests
    // ==========================================
    @Test
    public void testAlbumUI() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AlbumActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("ALBUM_NAME", "Test Album");
        intent.putExtra("ALBUM_ID", 1);

        try (ActivityScenario<AlbumActivity> scenario = ActivityScenario.launch(intent)) {
            // Check Title match
            onView(withId(R.id.album_title_txt)).check(matches(withText("Test Album")));

            // Check List View
            onView(withId(R.id.album_songs_list)).check(matches(isDisplayed()));

            // Check Navigation
            onView(withId(R.id.home_button_btn)).perform(click());
        }
    }

    // ==========================================
    // 8. AlbumReviewActivity Tests
    // ==========================================
    @Test
    public void testAlbumReviewSubmission() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AlbumReviewActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("ALBUM_ID", 100);
        intent.putExtra("ALBUM_NAME", "Review Album");

        try (ActivityScenario<AlbumReviewActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify Header
            onView(withId(R.id.review_header)).check(matches(withText("Review: Review Album")));

            // Input Data
            onView(withId(R.id.best_song_input)).perform(typeText("Best Song"), closeSoftKeyboard());
            onView(withId(R.id.worst_song_input)).perform(typeText("Worst Song"), closeSoftKeyboard());
            onView(withId(R.id.review_input)).perform(typeText("Great album!"), closeSoftKeyboard());

            // Submit
            onView(withId(R.id.submit_review_btn)).perform(click());
        }
    }

    // ==========================================
    // 9. BlindReviewActivity Tests
    // ==========================================
    @Test
    public void testBlindReviewUI() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BlindReviewActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<BlindReviewActivity> scenario = ActivityScenario.launch(intent)) {
            // Check elements
            onView(withId(R.id.blind_song_name)).check(matches(isDisplayed()));
            onView(withId(R.id.blind_play_btn)).check(matches(isDisplayed()));

            // Input review text
            onView(withId(R.id.blind_review_input)).perform(typeText("Mysterious track"), closeSoftKeyboard());

            // Click play (Tests logic for empty URL)
            onView(withId(R.id.blind_play_btn)).perform(click());

            // Click submit
            onView(withId(R.id.submit_blind_review_btn)).perform(click());
        }
    }
}