package dev.elelan.quotequiz.app

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.elelan.quotequiz.contract.quiz.QuizMode
import dev.elelan.quotequiz.contract.quiz.QuizResultDto
import dev.elelan.quotequiz.home.HomeNavigationState
import dev.elelan.quotequiz.home.HomeNavigator
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeNavigatorTest {
    @Test
    fun `selecting a tab updates selected tab`() {
        val state = navigationState()
        val navigator = HomeNavigator(state)

        navigator.selectTab(SettingsTab)

        assertEquals(SettingsTab, state.selectedTab)
    }

    @Test
    fun `back on non-start top level tab returns to start tab`() {
        val state = navigationState(selectedTab = ProfileTab)
        val navigator = HomeNavigator(state)

        navigator.goBack()

        assertEquals(QuizTab, state.selectedTab)
    }

    @Test
    fun `navigate adds nested route to current tab stack`() {
        val state = navigationState()
        val navigator = HomeNavigator(state)
        val resultRoute = QuizResultRoute(
            QuizResultDto(
                mode = QuizMode.BINARY,
                totalQuestions = 10,
                correctAnswers = 8,
                incorrectAnswers = 2,
                percentageScore = 80,
            ),
        )

        navigator.navigate(resultRoute)

        assertEquals(listOf(QuizTab, resultRoute), state.currentBackStack.toList())
    }

    private fun navigationState(selectedTab: NavKey = QuizTab): HomeNavigationState =
        HomeNavigationState(
            startTab = QuizTab,
            selectedTab = selectedTab,
            backStacks = mapOf(
                QuizTab to NavBackStack(QuizTab),
                SettingsTab to NavBackStack(SettingsTab),
                ProfileTab to NavBackStack(ProfileTab),
            ),
        )
}
