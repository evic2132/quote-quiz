package dev.elelan.quotequiz.feature.login

import androidx.compose.foundation.text.input.TextFieldState
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
import quotequiz.app.shared.generated.resources.login_error_email_required
import quotequiz.app.shared.generated.resources.login_error_password_required
import quotequiz.app.shared.generated.resources.login_error_unauthorized
import quotequiz.app.shared.generated.resources.not_implemented_forgot_password
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @Test
    fun `submit with empty fields shows required errors`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = createViewModel()

        viewModel.onAction(LoginAction.SubmitClicked)

        assertEquals(UiText.StringResourceId(Res.string.login_error_email_required), viewModel.uiState.value.emailError)
        assertEquals(UiText.StringResourceId(Res.string.login_error_password_required), viewModel.uiState.value.passwordError)
        Dispatchers.resetMain()
    }

    @Test
    fun `editing a field clears only that field error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = createViewModel()

        viewModel.onAction(LoginAction.SubmitClicked)
        setFieldText(viewModel.uiState.value.email, "demo@example.com")
        viewModel.onAction(LoginAction.EmailChanged)

        assertNull(viewModel.uiState.value.emailError)
        assertEquals(UiText.StringResourceId(Res.string.login_error_password_required), viewModel.uiState.value.passwordError)
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

        setFieldText(viewModel.uiState.value.email, "demo@example.com")
        viewModel.onAction(LoginAction.EmailChanged)
        setFieldText(viewModel.uiState.value.password, "password123")
        viewModel.onAction(LoginAction.PasswordChanged)
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

        setFieldText(viewModel.uiState.value.email, "demo@example.com")
        viewModel.onAction(LoginAction.EmailChanged)
        setFieldText(viewModel.uiState.value.password, "wrong-password")
        viewModel.onAction(LoginAction.PasswordChanged)
        viewModel.onAction(LoginAction.SubmitClicked)
        advanceUntilIdle()

        assertEquals(UiText.StringResourceId(Res.string.login_error_unauthorized), viewModel.uiState.value.loginError)
        Dispatchers.resetMain()
    }

    @Test
    fun `dead action emits not implemented snackbar effect`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModel = createViewModel()

        viewModel.onAction(LoginAction.ForgotPasswordClicked)

        assertEquals(
            LoginUiEffect.ShowMessage(UiText.StringResourceId(Res.string.not_implemented_forgot_password)),
            viewModel.uiEvent.first(),
        )
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

        setFieldText(viewModel.uiState.value.email, "demo@example.com")
        viewModel.onAction(LoginAction.EmailChanged)
        setFieldText(viewModel.uiState.value.password, "password123")
        viewModel.onAction(LoginAction.PasswordChanged)
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

    private fun setFieldText(
        state: TextFieldState,
        value: String,
    ) {
        state.edit {
            replace(0, length, value)
        }
    }

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
