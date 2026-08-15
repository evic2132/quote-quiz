package dev.elelan.quotequiz.app

import androidx.compose.runtime.mutableStateOf
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

        navigator.selectTab(AppRoute.SettingsTab)

        assertEquals(AppRoute.SettingsTab, state.selectedTab)
    }

    @Test
    fun `back on non-start top level tab returns to start tab`() {
        val state = navigationState(selectedTab = AppRoute.ProfileTab)
        val navigator = HomeNavigator(state)

        navigator.goBack()

        assertEquals(AppRoute.QuizTab, state.selectedTab)
    }

    @Test
    fun `navigate adds nested route to current tab stack`() {
        val state = navigationState()
        val navigator = HomeNavigator(state)
        val resultRoute = AppRoute.QuizResult(
            QuizResultDto(
                mode = QuizMode.BINARY,
                totalQuestions = 10,
                correctAnswers = 8,
                incorrectAnswers = 2,
                percentageScore = 80,
            ),
        )

        navigator.navigate(resultRoute)

        assertEquals(listOf(AppRoute.QuizTab, resultRoute), state.currentBackStack.toList())
    }

    private fun navigationState(selectedTab: AppRoute = AppRoute.QuizTab): HomeNavigationState =
        HomeNavigationState(
            startTab = AppRoute.QuizTab,
            selectedTabState = mutableStateOf(selectedTab),
            backStacks = mapOf(
                AppRoute.QuizTab to NavBackStack(AppRoute.QuizTab),
                AppRoute.SettingsTab to NavBackStack(AppRoute.SettingsTab),
                AppRoute.ProfileTab to NavBackStack(AppRoute.ProfileTab),
            ),
        )
}
