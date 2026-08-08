package com.studypulse.app.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.studypulse.app.presentation.components.WeeklyProgressRing
import com.studypulse.app.ui.theme.StudyPulseTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun weeklyProgressRingShowsCorrectHours() {
        composeTestRule.setContent {
            StudyPulseTheme {
                WeeklyProgressRing(fraction = 0.5f, totalHours = 5.0f)
            }
        }
        composeTestRule.onNodeWithText("5.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("/ 10 hrs").assertIsDisplayed()
    }

    @Test
    fun weeklyProgressRingShowsZeroWhenEmpty() {
        composeTestRule.setContent {
            StudyPulseTheme {
                WeeklyProgressRing(fraction = 0f, totalHours = 0f)
            }
        }
        composeTestRule.onNodeWithText("0.0").assertIsDisplayed()
    }
}
