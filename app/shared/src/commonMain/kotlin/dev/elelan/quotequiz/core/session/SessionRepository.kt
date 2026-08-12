package dev.elelan.quotequiz.core.session

import dev.elelan.quotequiz.contract.auth.UserDto
import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {
    val sessionState: StateFlow<SessionState>

    suspend fun restoreSession()

    suspend fun persistSession(token: String, user: UserDto)

    suspend fun logout()
}
