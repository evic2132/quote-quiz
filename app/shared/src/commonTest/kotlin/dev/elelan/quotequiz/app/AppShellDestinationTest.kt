package dev.elelan.quotequiz.app

import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.core.session.SessionState
import kotlin.test.Test
import kotlin.test.assertEquals

class AppContainerDestinationTest {
    @Test
    fun `loading state routes to splash`() {
        assertEquals(SplashRoute, SessionState.Loading.toRootRoute())
    }

    @Test
    fun `unauthenticated state routes to login`() {
        assertEquals(LoginRoute, SessionState.Unauthenticated.toRootRoute())
    }

    @Test
    fun `authenticated state routes to home`() {
        val state = SessionState.Authenticated(token = "token", user = demoUser())

        assertEquals(HomeRoute, state.toRootRoute())
    }

    private fun demoUser() =
        UserDto(
            id = 1,
            name = "Demo User",
            email = "demo@example.com",
        )
}
