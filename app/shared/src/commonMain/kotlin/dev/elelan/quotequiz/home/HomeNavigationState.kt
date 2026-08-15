package dev.elelan.quotequiz.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.savedstate.compose.serialization.serializers.MutableStateSerializer
import dev.elelan.quotequiz.app.AppRoute
import dev.elelan.quotequiz.app.MAIN_TABS
import dev.elelan.quotequiz.app.NavigationSerialization
import dev.elelan.quotequiz.app.rememberAppRouteBackStack

@Composable
fun rememberHomeNavigationState(
    startTab: AppRoute = AppRoute.QuizTab,
    topLevelTabs: Set<AppRoute> = MAIN_TABS,
): HomeNavigationState {
    val selectedTabState = rememberSerializable(
        startTab,
        topLevelTabs,
        serializer = MutableStateSerializer(AppRoute.serializer()),
        configuration = NavigationSerialization.configuration,
    ) {
        mutableStateOf(startTab)
    }
    val backStacks = topLevelTabs.associateWith { tab -> rememberAppRouteBackStack(tab) }

    return remember(startTab, topLevelTabs, selectedTabState) {
        HomeNavigationState(
            startTab = startTab,
            selectedTabState = selectedTabState,
            backStacks = backStacks,
        )
    }
}

class HomeNavigationState(
    val startTab: AppRoute,
    private val selectedTabState: MutableState<AppRoute>,
    val backStacks: Map<AppRoute, NavBackStack<AppRoute>>,
) {
    var selectedTab: AppRoute
        get() = selectedTabState.value
        set(value) {
            selectedTabState.value = value
        }

    val currentBackStack: NavBackStack<AppRoute>
        get() = backStacks.getValue(selectedTab)
}

class HomeNavigator(
    private val state: HomeNavigationState,
) {
    fun selectTab(tab: AppRoute) {
        check(tab in state.backStacks.keys) { "Unknown top-level tab: $tab" }
        state.selectedTab = tab
    }

    fun navigate(route: AppRoute) {
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
