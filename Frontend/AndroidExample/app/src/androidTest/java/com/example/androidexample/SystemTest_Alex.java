package com.example.androidexample;

import android.app.Activity;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.Root;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SystemTest_Alex {

    private static final String TEST_USERNAME = "testUser";
    private static final String JAM_MANAGER_USERNAME = "jamManager";

    @Before
    public void setup() {
        Intents.init();
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @After
    public void tearDown() {
        Intents.release();
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

    // MainActivity Tests
    @Test
    public void testMainToSignupNavigation() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.main_signup_btn)).perform(click());
            intended(hasComponent(SignupActivity.class.getName()));
        }
    }

    // SignupActivity Tests
    @Test
    public void testSignupActivity_Success() {
        try (ActivityScenario<SignupActivity> scenario = ActivityScenario.launch(SignupActivity.class)) {
            onView(withId(R.id.signup_username_edt)).perform(typeText("newUser"), closeSoftKeyboard());
            onView(withId(R.id.signup_password_edt)).perform(typeText("password"), closeSoftKeyboard());
            onView(withId(R.id.signup_confirm_edt)).perform(typeText("password"), closeSoftKeyboard());
            onView(withId(R.id.signup_signup_btn)).perform(click());
        }
    }

    @Test
    public void testSignupActivity_PasswordMismatch() {
        try (ActivityScenario<SignupActivity> scenario = ActivityScenario.launch(SignupActivity.class)) {
            onView(withId(R.id.signup_username_edt)).perform(typeText("newUser"), closeSoftKeyboard());
            onView(withId(R.id.signup_password_edt)).perform(typeText("password"), closeSoftKeyboard());
            onView(withId(R.id.signup_confirm_edt)).perform(typeText("wrongpassword"), closeSoftKeyboard());
            onView(withId(R.id.signup_signup_btn)).perform(click());
            onView(withId(R.id.signup_signup_btn)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testSignupActivity_AdminSignupFlow() {
        try (ActivityScenario<SignupActivity> scenario = ActivityScenario.launch(SignupActivity.class)) {
            onView(withId(R.id.account_type_spinner)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is("admin"))).perform(click());
            onView(withId(R.id.admin_password_layout)).check(matches(isDisplayed()));
            onView(withId(R.id.signup_username_edt)).perform(typeText("newAdmin"), closeSoftKeyboard());
            onView(withId(R.id.signup_password_edt)).perform(typeText("adminpass"), closeSoftKeyboard());
            onView(withId(R.id.signup_confirm_edt)).perform(typeText("adminpass"), closeSoftKeyboard());
            onView(withId(R.id.admin_password_edt)).perform(typeText("admin"), closeSoftKeyboard());
            onView(withId(R.id.signup_signup_btn)).perform(click());
        }
    }

    @Test
    public void testSignupActivity_NavigateToLogin() {
        try (ActivityScenario<SignupActivity> scenario = ActivityScenario.launch(SignupActivity.class)) {
            onView(withId(R.id.signup_login_btn)).perform(click());
            intended(hasComponent(LoginActivity.class.getName()));
        }
    }

    // CreateActivity Navigation Tests
    @Test
    public void testCreateActivity_NavigateToAllPages() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<CreateActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.create_review_btn)).perform(click());
            intended(hasComponent(CreateReviewActivity.class.getName()));
            pressBack();

            onView(withId(R.id.create_playlist_btn)).perform(click());
            intended(hasComponent(CreatePlaylistActivity.class.getName()));
            pressBack();

            onView(withId(R.id.home_button_btn)).perform(click());
            intended(hasComponent(HomeActivity.class.getName()));
            pressBack();

            onView(withId(R.id.music_button_btn)).perform(click());
            intended(hasComponent(MusicActivity.class.getName()));
            pressBack();

            onView(withId(R.id.jams_button_btn)).perform(click());
            intended(hasComponent(JamsActivity.class.getName()));
        }
    }

    // CreatePlaylistActivity Tests
    @Test
    public void testCreatePlaylistActivity_Success() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreatePlaylistActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<CreatePlaylistActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.playlist_name_edt)).perform(typeText("MyTestPlaylist"), closeSoftKeyboard());
            onView(withId(R.id.submit_btn)).perform(click());
        }
    }

    @Test
    public void testCreatePlaylistActivity_EmptyName() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreatePlaylistActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<CreatePlaylistActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.submit_btn)).perform(click());
            onView(withId(R.id.submit_btn)).check(matches(isDisplayed())); // Stays on page
        }
    }

    // CreateReviewActivity Tests
    @Test
    public void testCreateReviewActivity_InvalidRating() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateReviewActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<CreateReviewActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.song_name_edt)).perform(typeText("Test Song"), closeSoftKeyboard());
            onView(withId(R.id.arist_name_edt)).perform(typeText("Test Artist"), closeSoftKeyboard());
            onView(withId(R.id.rating_edt)).perform(typeText("6"), closeSoftKeyboard());
            onView(withId(R.id.submit_btn)).perform(click());
            onView(withId(R.id.submit_btn)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testCreateReviewActivity_BackButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateReviewActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<CreateReviewActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.back_btn)).perform(click());
        }
    }

    // AddSongsActivity Tests
    @Test
    public void testAddSongsActivity_SaveButtonNavigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddSongsActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME)
                .putExtra("PLAYLIST_NAME", "MyTestPlaylist")
                .putExtra("PREVIOUS_PAGE", "MY_PLAYLISTS");
        try (ActivityScenario<AddSongsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.save_btn)).perform(click());
            intended(hasComponent(MyPlaylistsActivity.class.getName()));
        }
    }

    @Test
    public void testCreatePlaylistActivity_BackButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreatePlaylistActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<CreatePlaylistActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.back_btn)).perform(click());
            intended(hasComponent(CreateActivity.class.getName()));
        }
    }

    @Test
    public void testAddSongsActivity_AddSong() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddSongsActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME)
                .putExtra("PLAYLIST_NAME", "MyTestPlaylist")
                .putExtra("PREVIOUS_PAGE", "MY_PLAYLISTS");
        try (ActivityScenario<AddSongsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.song_search_edt)).perform(typeText("Test Song"), closeSoftKeyboard());
            onView(withId(R.id.search_button_btn)).perform(click());
            // Assuming search returns at least one song, click the first "Add" button
            onView(withText("Add")).perform(click());
        }
    }

    // MyPlaylistsActivity Tests
    @Test
    public void testMyPlaylistsActivity_BackButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MyPlaylistsActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<MyPlaylistsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.back_btn)).perform(click());
        }
    }


    // CreateJamActivity Tests
    @Test
    public void testCreateJamActivity_EmptyFields() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateJamActivity.class)
                .putExtra("LOGGED_IN_USERNAME", JAM_MANAGER_USERNAME);
        try (ActivityScenario<CreateJamActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.submit_btn)).perform(click());
            onView(withId(R.id.submit_btn)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testCreateJamActivity_BackButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateJamActivity.class)
                .putExtra("LOGGED_IN_USERNAME", JAM_MANAGER_USERNAME);
        try (ActivityScenario<CreateJamActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.back_btn)).perform(click());
        }
    }

    // MusicActivity Tests
    @Test
    public void testMusicActivity_Navigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MusicActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<MusicActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.song_table)).check(matches(isDisplayed()));
            onView(withId(R.id.home_button_btn)).perform(click());
            intended(hasComponent(HomeActivity.class.getName()));
        }
    }

    // IndividualJamActivity Test
    @Test
    public void testIndividualJamActivity_SendMessage() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), IndividualJamActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME)
                .putExtra("JAM_NAME", "TestJam")
                .putExtra("JAM_ADMIN", JAM_MANAGER_USERNAME)
                .putExtra("APPROVAL_TYPE", "Manager");
        try (ActivityScenario<IndividualJamActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.dm_toolbar)).check(matches(isDisplayed()));
            onView(withId(R.id.message_input)).perform(typeText("Hello Jam!"), closeSoftKeyboard());
            onView(withId(R.id.send_btn)).perform(click());
            onView(withId(R.id.message_input)).check(matches(withText("")));
        }
    }

    @Test
    public void testIndividualJamActivity_SettingsButtonForAdmin() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), IndividualJamActivity.class)
                .putExtra("LOGGED_IN_USERNAME", JAM_MANAGER_USERNAME) // Logged in as admin
                .putExtra("JAM_NAME", "TestJam")
                .putExtra("JAM_ADMIN", JAM_MANAGER_USERNAME) // Admin of the jam
                .putExtra("APPROVAL_TYPE", "Manager");
        try (ActivityScenario<IndividualJamActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.jam_settings_btn)).check(matches(isDisplayed()));
            onView(withId(R.id.jam_settings_btn)).perform(click());
        }
    }

    // JamsActivity Tests
    @Test
    public void testJamsActivity_Navigation() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), JamsActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<JamsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.home_button_btn)).perform(click());
            intended(hasComponent(HomeActivity.class.getName()));
            pressBack();

            onView(withId(R.id.profile_button_btn)).perform(click());
            intended(hasComponent(ProfileActivity.class.getName()));
            pressBack();

            onView(withId(R.id.music_button_btn)).perform(click());
            intended(hasComponent(MusicActivity.class.getName()));
            pressBack();

            onView(withId(R.id.create_button_btn)).perform(click());
            intended(hasComponent(CreateActivity.class.getName()));
        }
    }


    // CreateActivity Tests
    @Test
    public void testCreateActivity_NavigateToProfile() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<CreateActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.profile_button_btn)).perform(click());
            intended(hasComponent(ProfileActivity.class.getName()));
        }
    }

    @Test
    public void testCreateReviewActivity_EmptyFields() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateReviewActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<CreateReviewActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.submit_btn)).perform(click());
            onView(withId(R.id.submit_btn)).check(matches(isDisplayed()));
        }
    }

    // IndividualJamActivity Tests
    @Test
    public void testIndividualJamActivity_NavigateUp() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), IndividualJamActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME)
                .putExtra("JAM_NAME", "TestJam")
                .putExtra("JAM_ADMIN", JAM_MANAGER_USERNAME)
                .putExtra("APPROVAL_TYPE", "Manager");
        try (ActivityScenario<IndividualJamActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withContentDescription("Navigate up")).perform(click());
        }
    }

    // AddSongsActivity Tests
    @Test
    public void testAddSongsActivity_SearchSong() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddSongsActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME)
                .putExtra("PLAYLIST_NAME", "MyTestPlaylist")
                .putExtra("PREVIOUS_PAGE", "MY_PLAYLISTS");
        try (ActivityScenario<AddSongsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.song_search_edt)).perform(typeText("Test Song"), closeSoftKeyboard());
            onView(withId(R.id.search_button_btn)).perform(click());
            onView(withId(R.id.song_search_table)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testAddSongsActivity_BackButton() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), AddSongsActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME)
                .putExtra("PLAYLIST_NAME", "MyTestPlaylist")
                .putExtra("PREVIOUS_PAGE", "MY_PLAYLISTS");
        try (ActivityScenario<AddSongsActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.back_btn)).perform(click());
            intended(hasComponent(MyPlaylistsActivity.class.getName()));
        }
    }


    @Test
    public void testCreateReviewActivity_PartialFields() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CreateReviewActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME);
        try (ActivityScenario<CreateReviewActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.song_name_edt)).perform(typeText("Test Song"), closeSoftKeyboard());
            onView(withId(R.id.submit_btn)).perform(click());
            onView(withId(R.id.submit_btn)).check(matches(isDisplayed()));
        }
    }


    @Test
    public void testIndividualJamActivity_SettingsButtonForNonAdmin() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), IndividualJamActivity.class)
                .putExtra("LOGGED_IN_USERNAME", TEST_USERNAME) // Logged in as non-admin
                .putExtra("JAM_NAME", "TestJam")
                .putExtra("JAM_ADMIN", JAM_MANAGER_USERNAME) // Admin of the jam
                .putExtra("APPROVAL_TYPE", "Manager");
        try (ActivityScenario<IndividualJamActivity> scenario = ActivityScenario.launch(intent)) {
            onView(withId(R.id.jam_settings_btn)).check(matches(not(isDisplayed())));
        }
    }

    public static class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                if (windowToken == appToken) {
                    return true;
                }
            }
            return false;
        }
    }
}
