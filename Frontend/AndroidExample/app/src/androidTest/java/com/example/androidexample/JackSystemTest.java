package com.example.androidexample;

import android.content.Intent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class JackSystemTest {

    @Test
    public void testLoginFlow() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), LoginActivity.class);
        try (ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.login_username_edt)).perform(typeText("testuser"), closeSoftKeyboard());
            onView(withId(R.id.login_password_edt)).perform(typeText("pass123"), closeSoftKeyboard());
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

    @Test
    public void testHomeAllButtons() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), HomeActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.music_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.jams_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.profile_button_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.blind_review_btn)).perform(click());
        }
    }

    @Test
    public void testProfileEditAndDelete() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("PROFILE_TO_VIEW", "testuser");

        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.profile_bio_edt)).perform(clearText(), typeText("New Bio"), closeSoftKeyboard());
            onView(withId(R.id.profile_update_btn)).perform(click());
            onView(withId(R.id.profile_delete_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.profile_delete_btn)).perform(click());
        }
    }

    @Test
    public void testProfileReadOnly() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("PROFILE_TO_VIEW", "otheruser");

        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.profile_update_btn)).check(matches(not(isDisplayed())));
        }
    }

    @Test
    public void testProfileToolbarNavigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ProfileActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("PROFILE_TO_VIEW", "testuser");

        try (ActivityScenario<ProfileActivity> scenario = ActivityScenario.launch(intent)) {
            try {
                onView(isRoot()).perform(pressBack());
            } catch (NoActivityResumedException e) {
            }
        }
    }

    @Test
    public void testFriendsUI() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendsActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<FriendsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.add_friend_button)).check(matches(isDisplayed()));
            onView(withId(R.id.pending_requests_button)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testFriendsNavigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FriendsActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<FriendsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.home_button_btn)).perform(click());
        }
    }

    @Test
    public void testAlbumUI() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AlbumActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("ALBUM_NAME", "Test Album");
        intent.putExtra("ALBUM_ID", 1);

        try (ActivityScenario<AlbumActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.album_title_txt)).check(matches(isDisplayed()));
            onView(withId(R.id.home_button_btn)).perform(click());
        }
    }

    @Test
    public void testBlindReviewUI() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BlindReviewActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<BlindReviewActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.blind_play_btn)).perform(click());
        }
    }

    @Test
    public void testSearchFunctionality() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), SearchActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");

        try (ActivityScenario<SearchActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.search_type_spinner)).check(matches(isDisplayed()));
            onView(withId(R.id.search_submit_button)).perform(click());

            try {
                onView(isRoot()).perform(pressBack());
            } catch (NoActivityResumedException e) {
            }
        }
    }

    @Test
    public void testDMSendingAndBack() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), DMActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "user1");
        intent.putExtra("FRIEND_USERNAME", "user2");

        try (ActivityScenario<DMActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.chat_recycler_view)).check(matches(isDisplayed()));
            onView(withId(R.id.message_input)).perform(typeText("Hello"), closeSoftKeyboard());
            onView(withId(R.id.send_button)).perform(click());

            try {
                onView(isRoot()).perform(pressBack());
            } catch (NoActivityResumedException e) {
            }
        }
    }

    @Test
    public void testAlbumReviewSubmission() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AlbumReviewActivity.class);
        intent.putExtra("LOGGED_IN_USERNAME", "testuser");
        intent.putExtra("ALBUM_ID", 100);
        intent.putExtra("ALBUM_NAME", "Review Album");

        try (ActivityScenario<AlbumReviewActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.review_header)).check(matches(isDisplayed()));
            onView(withId(R.id.best_song_input)).perform(typeText("Best Song"), closeSoftKeyboard());
            onView(withId(R.id.submit_review_btn)).perform(click());
            onView(withId(R.id.jams_button_btn)).perform(click());
        }
    }

    @Test
    public void testChatAdapterLogic() {
        ChatMessage msgSent = new ChatMessage("me", "Hi");
        ChatMessage msgReceived = new ChatMessage("friend", "Hello");

        assertEquals("me", msgSent.getSender());
        assertEquals("Hi", msgSent.getContent());

        List<ChatMessage> list = new ArrayList<>();
        list.add(msgSent);
        list.add(msgReceived);

        ChatAdapter adapter = new ChatAdapter(list, "me");

        assertEquals(2, adapter.getItemCount());
        assertEquals(1, adapter.getItemViewType(0));
        assertEquals(2, adapter.getItemViewType(1));
    }
}