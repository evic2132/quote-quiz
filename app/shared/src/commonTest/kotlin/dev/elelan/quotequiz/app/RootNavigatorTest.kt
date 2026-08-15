package dev.elelan.quotequiz.app

import androidx.navigation3.runtime.NavBackStack
import kotlin.test.Test
import kotlin.test.assertEquals

class RootNavigatorTest {
    @Test
    fun `moveTo replaces splash with target route`() {
        val state = RootNavigationState(
            backStack = NavBackStack(AppRoute.Splash),
        )
        val navigator = RootNavigator(state)

        navigator.moveTo(AppRoute.Login)

        assertEquals(listOf(AppRoute.Login), state.backStack.toList())
    }

    @Test
    fun `moveTo keeps single matching route stable`() {
        val state = RootNavigationState(
            backStack = NavBackStack(AppRoute.Home),
        )
        val navigator = RootNavigator(state)

        navigator.moveTo(AppRoute.Home)

        assertEquals(listOf(AppRoute.Home), state.backStack.toList())
    }
}
