package com.example.android.bakingapp;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;


import com.example.android.bakingapp.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityBasicTest {

    public static final String INGREDIENTS_TAB = "Ingredients";

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickGridViewItem_OpenRecipeDetailActivity(){

        //1.find view
        //2.Perform action on view
        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // recyclerview item and clicks it.
        onView(withId(R.id.rv_Recipes))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //3.check if view does want i wanted
        // Checks that the OrderActivity opens with the correct tea name displayed
        onView(withId(R.id.tv_ingredients_tab)).check(matches(withText(INGREDIENTS_TAB)));

    }
}
