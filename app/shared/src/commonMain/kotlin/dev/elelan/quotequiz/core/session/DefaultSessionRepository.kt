package dev.elelan.quotequiz.core.session

import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.core.api.ProfileApi
import dev.elelan.quotequiz.core.network.ApiError
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DefaultSessionRepository(
    private val tokenStorage: TokenStorage,
    private val profileApi: ProfileApi,
) : SessionRepository {
    private val mutableSessionState = MutableStateFlow<SessionState>(SessionState.Loading)

    override val sessionState: StateFlow<SessionState> = mutableSessionState.asStateFlow()

    override suspend fun restoreSession() {
        val token = tokenStorage.get()
        if (token == null) {
            mutableSessionState.value = SessionState.Unauthenticated
            return
        }

        mutableSessionState.value =
            when (val result = profileApi.getCurrentUser(token)) {
                is ApiResult.Success -> SessionState.Authenticated(token = token, user = result.value)
                is ApiResult.Failure -> {
                    if (result.error == ApiError.Unauthorized) {
                        tokenStorage.clear()
                    }
                    SessionState.Unauthenticated
                }
            }
    }

    override suspend fun persistSession(token: String, user: UserDto) {
        tokenStorage.set(token)
        mutableSessionState.value = SessionState.Authenticated(token = token, user = user)
    }

    override suspend fun logout() {
        tokenStorage.clear()
        mutableSessionState.value = SessionState.Unauthenticated
    }
}
