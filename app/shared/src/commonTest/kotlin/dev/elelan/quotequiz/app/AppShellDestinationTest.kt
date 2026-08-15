package dev.elelan.quotequiz.app

import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.core.session.SessionState
import kotlin.test.Test
import kotlin.test.assertEquals

class AppContainerDestinationTest {
    @Test
    fun `loading state routes to splash`() {
        assertEquals(AppRoute.Splash, SessionState.Loading.toAppRoute())
    }

    @Test
    fun `unauthenticated state routes to login`() {
        assertEquals(AppRoute.Login, SessionState.Unauthenticated.toAppRoute())
    }

    @Test
    fun `authenticated state routes to home`() {
        val state = SessionState.Authenticated(token = "token", user = demoUser())

        assertEquals(AppRoute.Home, state.toAppRoute())
    }

    private fun demoUser() =
        UserDto(
            id = 1,
            name = "Demo User",
            email = "demo@example.com",
        )
}
