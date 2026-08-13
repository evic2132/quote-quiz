package dev.elelan.quotequiz.feature.login

import dev.elelan.quotequiz.contract.auth.LoginRequest
import dev.elelan.quotequiz.contract.auth.LoginResponse
import dev.elelan.quotequiz.contract.auth.UserDto
import dev.elelan.quotequiz.core.api.AuthApi
import dev.elelan.quotequiz.core.network.ApiError
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.session.SessionRepository
import dev.elelan.quotequiz.core.session.SessionState
import dev.elelan.quotequiz.core.ui.UiText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import quotequiz.app.shared.generated.resources.Res
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @Test
    fun `submit with empty fields shows required errors`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = createViewModel()
        val validationResult = CredentialsValidator().validate("", "")

        viewModel.onAction(LoginAction.SubmitClicked)

        assertEquals(validationResult.emailError, viewModel.uiState.value.emailError)
        assertEquals(validationResult.passwordError, viewModel.uiState.value.passwordError)
        Dispatchers.resetMain()
    }

    @Test
    fun `editing a field clears only that field error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(LoginAction.SubmitClicked)
        viewModel.onAction(LoginAction.EmailChanged("demo@example.com"))

        assertNull(viewModel.uiState.value.emailError)
        assertEquals(CredentialsValidator().validate("", "").passwordError, viewModel.uiState.value.passwordError)
        Dispatchers.resetMain()
    }

    @Test
    fun `successful login persists session`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val sessionRepository = FakeSessionRepository()
        val authApi = FakeAuthApi(
            result = ApiResult.Success(
                LoginResponse(
                    token = "token-123",
                    user = demoUser(),
                ),
            ),
        )
        val viewModel = createViewModel(authApi = authApi, sessionRepository = sessionRepository)

        viewModel.onAction(LoginAction.EmailChanged("demo@example.com"))
        viewModel.onAction(LoginAction.PasswordChanged("password123"))
        viewModel.onAction(LoginAction.SubmitClicked)
        advanceUntilIdle()

        assertEquals(LoginRequest("demo@example.com", "password123"), authApi.lastRequest)
        assertEquals("token-123", sessionRepository.persistedToken)
        assertEquals(SessionState.Authenticated("token-123", demoUser()), sessionRepository.sessionState.value)
        assertEquals(false, viewModel.uiState.value.isSubmitting)
        Dispatchers.resetMain()
    }

    @Test
    fun `unauthorized login shows user friendly error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = createViewModel(authApi = FakeAuthApi(result = ApiResult.Failure(ApiError.Unauthorized)))

        viewModel.onAction(LoginAction.EmailChanged("demo@example.com"))
        viewModel.onAction(LoginAction.PasswordChanged("wrong-password"))
        viewModel.onAction(LoginAction.SubmitClicked)
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.loginError)
        Dispatchers.resetMain()
    }

    @Test
    fun `dead action emits not implemented snackbar effect`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = createViewModel()

        viewModel.onAction(LoginAction.ForgotPasswordClicked)

        assertIs<LoginUiEffect.ShowMessage>(viewModel.uiEvent.first())
        Dispatchers.resetMain()
    }

    @Test
    fun `duplicate submit does not issue duplicate login request`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val authApi = FakeAuthApi(resultProvider = {
            delay(50)
            ApiResult.Success(LoginResponse("token-123", demoUser()))
        })
        val viewModel = createViewModel(authApi = authApi)

        viewModel.onAction(LoginAction.EmailChanged("demo@example.com"))
        viewModel.onAction(LoginAction.PasswordChanged("password123"))
        viewModel.onAction(LoginAction.SubmitClicked)
        viewModel.onAction(LoginAction.SubmitClicked)
        advanceUntilIdle()

        assertEquals(1, authApi.callCount)
        Dispatchers.resetMain()
    }

    private fun createViewModel(
        authApi: FakeAuthApi = FakeAuthApi(),
        sessionRepository: FakeSessionRepository = FakeSessionRepository(),
    ): LoginViewModel = LoginViewModel(
        authApi = authApi,
        sessionRepository = sessionRepository,
    )

    private fun demoUser() = UserDto(
        id = 1,
        name = "Demo User",
        email = "demo@example.com",
    )

    private class FakeAuthApi(
        private val result: ApiResult<LoginResponse> = ApiResult.Success(
            LoginResponse(
                token = "token-123",
                user = UserDto(1, "Demo User", "demo@example.com"),
            ),
        ),
        private val resultProvider: (suspend () -> ApiResult<LoginResponse>)? = null,
    ) : AuthApi {
        var lastRequest: LoginRequest? = null
            private set

        var callCount: Int = 0
            private set

        override suspend fun login(request: LoginRequest): ApiResult<LoginResponse> {
            lastRequest = request
            callCount += 1
            return resultProvider?.invoke() ?: result
        }
    }

    private class FakeSessionRepository : SessionRepository {
        private val mutableState = MutableStateFlow<SessionState>(SessionState.Unauthenticated)

        override val sessionState: StateFlow<SessionState> = mutableState

        var persistedToken: String? = null
            private set

        override suspend fun restoreSession() = Unit

        override suspend fun persistSession(token: String, user: UserDto) {
            persistedToken = token
            mutableState.value = SessionState.Authenticated(token, user)
        }

        override suspend fun logout() {
            mutableState.value = SessionState.Unauthenticated
        }
    }
}
