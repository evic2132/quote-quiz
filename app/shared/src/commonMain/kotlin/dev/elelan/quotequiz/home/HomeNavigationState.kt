package dev.elelan.quotequiz.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.elelan.quotequiz.app.MAIN_TABS
import dev.elelan.quotequiz.app.ProfileTab
import dev.elelan.quotequiz.app.QuizTab
import dev.elelan.quotequiz.app.SettingsTab

@Composable
fun rememberHomeNavigationState(
    startTab: NavKey = QuizTab,
    topLevelTabs: Set<NavKey> = MAIN_TABS,
): HomeNavigationState =
    remember(startTab, topLevelTabs) {
        HomeNavigationState(
            startTab = startTab,
            selectedTab = startTab,
            backStacks = topLevelTabs.associateWith { tab -> NavBackStack(tab) },
        )
    }

class HomeNavigationState(
    val startTab: NavKey,
    selectedTab: NavKey,
    val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var selectedTab: NavKey by mutableStateOf(selectedTab)

    val currentBackStack: NavBackStack<NavKey>
        get() = backStacks.getValue(selectedTab)
}

class HomeNavigator(
    private val state: HomeNavigationState,
) {
    fun selectTab(tab: NavKey) {
        check(tab in state.backStacks.keys) { "Unknown top-level tab: $tab" }
        state.selectedTab = tab
    }

    fun navigate(route: NavKey) {
        state.currentBackStack.add(route)
    }

    fun goBack() {
        val currentStack = state.currentBackStack
        if (currentStack.last() == state.selectedTab) {
            state.selectedTab = state.startTab
        } else {
            currentStack.removeLastOrNull()
        }
    }
}
