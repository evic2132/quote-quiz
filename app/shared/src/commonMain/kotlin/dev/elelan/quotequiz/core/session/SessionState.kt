package dev.elelan.quotequiz.core.session

import dev.elelan.quotequiz.contract.auth.UserDto

sealed interface SessionState {
    data object Loading : SessionState

    data object Unauthenticated : SessionState

    data class Authenticated(
        val token: String,
        val user: UserDto,
    ) : SessionState
}
