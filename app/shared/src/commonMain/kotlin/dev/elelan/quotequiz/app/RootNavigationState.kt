package dev.elelan.quotequiz.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack

@Composable
fun rememberRootNavigationState(
    startRoute: RootRoute = SplashRoute,
): RootNavigationState =
    remember(startRoute) {
        RootNavigationState(
            startRoute = startRoute,
            backStack = NavBackStack(startRoute),
        )
    }

class RootNavigationState(
    val startRoute: RootRoute,
    val backStack: NavBackStack<RootRoute>,
)

class RootNavigator(
    private val state: RootNavigationState,
) {
    fun moveTo(route: RootRoute) {
        if (state.backStack.lastOrNull() == route && state.backStack.size == 1) {
            return
        }

        state.backStack.clear()
        state.backStack.add(route)
    }

    fun goBack() {
        state.backStack.removeLastOrNull()
    }
}
