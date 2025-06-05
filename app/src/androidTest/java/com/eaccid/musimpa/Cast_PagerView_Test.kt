package com.eaccid.musimpa

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eaccid.musimpa.domain.model.Actor
import com.eaccid.musimpa.ui.moviedetailsscreen.CastPagerView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Cast_PagerView_Test {
    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun castPagerView_displaysActorNamesAndCharacters() {
        val mockActors = listOf(
            Actor(
                id = 1,
                name = "Tom Hanks",
                character = "Forrest Gump",
                profilePath = "/path.jpg"
            ),
            Actor(
                id = 2,
                name = "Robin Wright",
                character = "Jenny",
                profilePath = "/path2.jpg"
            )
        )

        composeTestRule.setContent {
            CastPagerView(actors = mockActors)
        }

        composeTestRule.onNodeWithText("Tom Hanks").assertIsDisplayed()
        composeTestRule.onNodeWithText("Forrest Gump").assertIsDisplayed()
    }

    @Test
    fun castPagerView_displaysImageWithCorrectContentDescription() {
        val actors = listOf(
            Actor(id = 1, name = "Actor 1", character = "Role", profilePath = "path/to/image.jpg")
        )

        composeTestRule.setContent {
            CastPagerView(actors)
        }

        composeTestRule.onNodeWithContentDescription("path/to/image.jpg").assertExists()
    }

    fun castPagerView_swipeToNextPage_displaysNextActor() {
        val actors = listOf(
            Actor(id = 1, name = "Actor 1", character = "Hero", profilePath = "path1"),
            Actor(id = 2, name = "Actor 2", character = "Villain", profilePath = "path2")
        )

        composeTestRule.setContent {
            CastPagerView(actors)
        }

        composeTestRule.onNodeWithText("Actor 1")
            .performTouchInput { swipeLeft() }

        composeTestRule.onNodeWithText("Actor 2").assertExists()
    }

}
