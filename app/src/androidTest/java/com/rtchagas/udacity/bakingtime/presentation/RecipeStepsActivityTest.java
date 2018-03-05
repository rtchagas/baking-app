package com.rtchagas.udacity.bakingtime.presentation;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.rtchagas.udacity.bakingtime.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeStepsActivityTest {

    @Rule
    public ActivityTestRule<RecipesListActivity> mActivityTestRule
            = new ActivityTestRule<>(RecipesListActivity.class);

    @Test
    public void recipeStepsActivityTest() {

        // Give some seconds to load all recipes...
        sleep(6000);

        // Click at the last recipe (Cheesecake)
        ViewInteraction recipesList = onView(withId(R.id.recyclerview_recipes));
        recipesList.perform(scrollToPosition(3));
        recipesList.perform(actionOnItemAtPosition(3, click()));
        sleep(800);

        // Click at first step, just after the ingredients
        onView(withId(R.id.recyclerview_steps)).perform(actionOnItemAtPosition(1, click()));
        sleep(1000);

        // Check if FAB menu is there
        onView(withId(R.id.fab_menu_steps)).check(matches(isDisplayed()));

        // Press the FAB menu
        onView(withId(R.id.fab_expand_menu_button)).perform(click());

        // Check if "Previous step" was shown
        onView(withId(R.id.fab_item_step_previous)).check(matches(isDisplayed()));

        // Check if "Next step" was shown
        onView(withId(R.id.fab_item_step_next)).check(matches(isDisplayed()));

        // Try to go to the previous step
        onView(withId(R.id.fab_item_step_previous)).perform(click());

        // Check if snackbar was show
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.step_jump_first)))
                .check(matches(isDisplayed()));

        // Navigate until the last step
        for (int i = 0; i < 12; i++) {
            goToNextStep();
        }

        // Try to go to the one step after the last one
        onView(withId(R.id.fab_item_step_next)).perform(click());

        // Check if snackbar was show
        onView(allOf(withId(android.support.design.R.id.snackbar_text),
                withText(R.string.step_jump_last)))
                .check(matches(isDisplayed()));

    }

    private void goToNextStep() {
        ViewInteraction fabNext = onView(withId(R.id.fab_item_step_next));
        fabNext.perform(click());
        sleep(1000);
    }

    private void sleep(long delay) {
        try {
            Thread.sleep(delay);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}