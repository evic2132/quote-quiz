package dev.elelan.quotequiz.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.foundation.text.input.TextFieldState
import dev.elelan.quotequiz.contract.auth.LoginRequest
import dev.elelan.quotequiz.core.api.AuthApi
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.network.toUiText
import dev.elelan.quotequiz.core.session.SessionRepository
import dev.elelan.quotequiz.core.ui.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.login_error_email_required
import quotequiz.app.shared.generated.resources.login_error_http
import quotequiz.app.shared.generated.resources.login_error_network
import quotequiz.app.shared.generated.resources.login_error_password_required
import quotequiz.app.shared.generated.resources.login_error_unauthorized
import quotequiz.app.shared.generated.resources.login_error_unknown
import quotequiz.app.shared.generated.resources.not_implemented_forgot_password
import quotequiz.app.shared.generated.resources.not_implemented_remember_me
import quotequiz.app.shared.generated.resources.not_implemented_sign_up

class LoginViewModel(
    private val authApi: AuthApi,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    val uiState: StateFlow<LoginUiState>
        field = MutableStateFlow(LoginUiState())

    private val _uiEvent = Channel<LoginUiEffect>()
    val uiEvent: Flow<LoginUiEffect> = _uiEvent.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.EmailChanged -> clearEmailError()
            LoginAction.PasswordChanged -> clearPasswordError()
            LoginAction.SubmitClicked -> submit()
            LoginAction.RememberMeClicked -> emitMessage(UiText.StringResourceId(Res.string.not_implemented_remember_me))
            LoginAction.ForgotPasswordClicked -> emitMessage(UiText.StringResourceId(Res.string.not_implemented_forgot_password))
            LoginAction.SignUpClicked -> emitMessage(UiText.StringResourceId(Res.string.not_implemented_sign_up))
        }
    }

    private fun clearEmailError() {
        uiState.update {
            it.copy(
                emailError = null,
                loginError = null,
            )
        }
    }

    private fun clearPasswordError() {
        uiState.update {
            it.copy(
                passwordError = null,
                loginError = null,
            )
        }
    }

    private fun submit() {
        val currentState = uiState.value
        if (currentState.isSubmitting) return

        val email = currentState.email.text.toString()
        val password = currentState.password.text.toString()
        val emailError =
            if (email.isBlank()) UiText.StringResourceId(Res.string.login_error_email_required) else null
        val passwordError =
            if (password.isBlank()) UiText.StringResourceId(Res.string.login_error_password_required) else null
        if (emailError != null || passwordError != null) {
            uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    loginError = null,
                )
            }
            return
        }

        uiState.update { it.copy(isSubmitting = true, loginError = null) }

        viewModelScope.launch {
            when (
                val result = authApi.login(
                    LoginRequest(
                        email = email.trim(),
                        password = password,
                    ),
                )
            ) {
                is ApiResult.Success -> {
                    sessionRepository.persistSession(
                        token = result.value.token,
                        user = result.value.user,
                    )
                    uiState.update { it.copy(isSubmitting = false) }
                }

                is ApiResult.Failure -> {
                    uiState.update {
                        it.copy(
                            isSubmitting = false,
                            loginError = result.error.toUiText(
                                network = UiText.StringResourceId(Res.string.login_error_network),
                                unauthorized = UiText.StringResourceId(Res.string.login_error_unauthorized),
                                http = UiText.StringResourceId(Res.string.login_error_http),
                                unknown = UiText.StringResourceId(Res.string.login_error_unknown),
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun emitMessage(message: UiText) {
        viewModelScope.launch {
            _uiEvent.send(LoginUiEffect.ShowMessage(message))
        }
    }
}
