package dev.elelan.quotequiz.app

import androidx.navigation3.runtime.NavBackStack
import kotlin.test.Test
import kotlin.test.assertEquals

class RootNavigatorTest {
    @Test
    fun `moveTo replaces splash with target route`() {
        val state = RootNavigationState(
            startRoute = SplashRoute,
            backStack = NavBackStack<RootRoute>(SplashRoute),
        )
        val navigator = RootNavigator(state)

        navigator.moveTo(LoginRoute)

        assertEquals(listOf(LoginRoute), state.backStack.toList())
    }

    @Test
    fun `moveTo keeps single matching route stable`() {
        val state = RootNavigationState(
            startRoute = SplashRoute,
            backStack = NavBackStack<RootRoute>(HomeRoute),
        )
        val navigator = RootNavigator(state)

        navigator.moveTo(HomeRoute)

        assertEquals(listOf(HomeRoute), state.backStack.toList())
    }
}
