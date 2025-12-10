package com.example.androidexample;

import android.app.Activity;
import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

// Fixed Imports
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
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AlexSystemTest {
    /***
     * Test 1: Test Signup Button
     */
    @Test
    public void testSignupButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.main_signup_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.main_signup_btn)).perform(click());
        }
    }

    /***
     * Test 2: Test Create Jam
     */
    @Test
    public void testCreateJamEntry() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateJamActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "TestUser");
        intent.putExtra("PROFILE_TO_VIEW", "TestUser");

        try (ActivityScenario<CreateJamActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.jam_name_edt)).perform(typeText("TestJam"), closeSoftKeyboard());
            onView(withId(R.id.genre_edt)).perform(typeText("TestGenre"), closeSoftKeyboard());
            onView(withId(R.id.genre_edt)).check(matches(withText("TestGenre")));
            onView(withId(R.id.submit_btn)).check(matches(isEnabled()));
            onView(withId(R.id.submit_btn)).perform(click());
        }
    }

    /***
     * Test 3: Test Create Playlist
     */
    @Test
    public void testCreatePlaylist() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreatePlaylistActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "TestUser");
        intent.putExtra("PROFILE_TO_VIEW", "TestUser");

        try (ActivityScenario<CreatePlaylistActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.playlist_name_edt)).perform(typeText("TestPlaylist"), closeSoftKeyboard());
            onView(withId(R.id.playlist_name_edt)).check(matches(withText("TestPlaylist")));
            onView(withId(R.id.submit_btn)).check(matches(isEnabled()));

            onView(withId(R.id.submit_btn)).perform(click());
        }
    }

    /***
     * Test 4: Test Home Buttons are Displayed
     */
    @Test
    public void testHomeButtons() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HomeActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "TestUser");

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.profile_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.music_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.jams_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.create_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.my_playlists_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.friends_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.search_button)).check(matches(isDisplayed()));
        }
    }

}
