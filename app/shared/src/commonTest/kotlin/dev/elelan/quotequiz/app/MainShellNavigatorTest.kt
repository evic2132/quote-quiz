package dev.elelan.quotequiz.app

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlin.test.Test
import kotlin.test.assertEquals

class MainShellNavigatorTest {
    @Test
    fun `selecting a tab updates selected tab`() {
        val state = navigationState()
        val navigator = MainShellNavigator(state)

        navigator.selectTab(SettingsTab)

        assertEquals(SettingsTab, state.selectedTab)
    }

    @Test
    fun `back on non-start top level tab returns to start tab`() {
        val state = navigationState(selectedTab = ProfileTab)
        val navigator = MainShellNavigator(state)

        navigator.goBack()

        assertEquals(QuizTab, state.selectedTab)
    }

    private fun navigationState(selectedTab: NavKey = QuizTab): MainShellNavigationState =
        MainShellNavigationState(
            startTab = QuizTab,
            selectedTab = selectedTab,
            backStacks = mapOf(
                QuizTab to NavBackStack(QuizTab),
                SettingsTab to NavBackStack(SettingsTab),
                ProfileTab to NavBackStack(ProfileTab),
            ),
        )
}
