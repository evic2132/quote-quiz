package dev.elelan.quotequiz.core.session

import com.russhwolf.settings.MapSettings
import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.core.api.ProfileApi
import dev.elelan.quotequiz.core.network.ApiError
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.storage.SettingsTokenStorage
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class DefaultSessionRepositoryTest {
    @Test
    fun `no token startup becomes unauthenticated without hitting backend`() = runTest {
        val profileApi = FakeProfileApi()
        val repository = createRepository(profileApi = profileApi)

        repository.restoreSession()

        assertEquals(0, profileApi.callCount)
        assertEquals(SessionState.Unauthenticated, repository.sessionState.value)
    }

    @Test
    fun `valid token startup restores authenticated session`() = runTest {
        val expectedUser = demoUser()
        val profileApi = FakeProfileApi(ApiResult.Success(expectedUser))
        val tokenStorage = SettingsTokenStorage(MapSettings())
        tokenStorage.set("valid-token")
        val repository = DefaultSessionRepository(tokenStorage = tokenStorage, profileApi = profileApi)

        repository.restoreSession()

        assertEquals(1, profileApi.callCount)
        assertEquals("valid-token", profileApi.lastToken)
        assertEquals(
            SessionState.Authenticated(
                token = "valid-token",
                user = expectedUser,
            ),
            repository.sessionState.value,
        )
    }

    @Test
    fun `invalid token startup clears stored token and becomes unauthenticated`() = runTest {
        val tokenStorage = SettingsTokenStorage(MapSettings())
        tokenStorage.set("expired-token")
        val repository = DefaultSessionRepository(
            tokenStorage = tokenStorage,
            profileApi = FakeProfileApi(ApiResult.Failure(ApiError.Unauthorized)),
        )

        repository.restoreSession()

        assertEquals(SessionState.Unauthenticated, repository.sessionState.value)
        assertNull(tokenStorage.get())
    }

    @Test
    fun `logout clears session and stored token`() = runTest {
        val tokenStorage = SettingsTokenStorage(MapSettings())
        val repository = DefaultSessionRepository(
            tokenStorage = tokenStorage,
            profileApi = FakeProfileApi(ApiResult.Success(demoUser())),
        )

        repository.persistSession(token = "persisted-token", user = demoUser())
        repository.logout()

        assertEquals(SessionState.Unauthenticated, repository.sessionState.value)
        assertNull(tokenStorage.get())
    }

    private fun createRepository(profileApi: FakeProfileApi = FakeProfileApi()): DefaultSessionRepository =
        DefaultSessionRepository(
            tokenStorage = SettingsTokenStorage(MapSettings()),
            profileApi = profileApi,
        )

    private fun demoUser() =
        UserDto(
            id = 1,
            name = "Demo User",
            email = "demo@example.com",
        )

    private class FakeProfileApi(
        private val result: ApiResult<UserDto> = ApiResult.Success(
            UserDto(
                id = 1,
                name = "Demo User",
                email = "demo@example.com",
            ),
        ),
    ) : ProfileApi {
        var callCount: Int = 0
            private set

        var lastToken: String? = null
            private set

        override suspend fun getCurrentUser(token: String): ApiResult<UserDto> {
            callCount += 1
            lastToken = token
            return result
        }
    }
}
