package dev.elelan.quotequiz.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack

@Composable
fun rememberRootNavigationState(startRoute: AppRoute = AppRoute.Splash): RootNavigationState {
    val backStack = rememberAppRouteBackStack(startRoute)

    return remember(startRoute, backStack) {
        RootNavigationState(
            //startRoute = startRoute,
            backStack = backStack,
        )
    }
}

class RootNavigationState(
    //val startRoute: AppRoute,
    val backStack: NavBackStack<AppRoute>,
)

class RootNavigator(
    private val state: RootNavigationState,
) {
    fun moveTo(route: AppRoute) {
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
