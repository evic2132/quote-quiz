package dev.elelan.quotequiz.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.elelan.quotequiz.contract.auth.LoginRequest
import dev.elelan.quotequiz.core.api.AuthApi
import dev.elelan.quotequiz.core.network.ApiResult
import dev.elelan.quotequiz.core.network.toUiText
import dev.elelan.quotequiz.core.session.SessionRepository
import dev.elelan.quotequiz.ui.core.UiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.login_error_http
import quotequiz.app.shared.generated.resources.login_error_network
import quotequiz.app.shared.generated.resources.login_error_unauthorized
import quotequiz.app.shared.generated.resources.login_error_unknown
import quotequiz.app.shared.generated.resources.not_implemented_forgot_password
import quotequiz.app.shared.generated.resources.not_implemented_remember_me
import quotequiz.app.shared.generated.resources.not_implemented_sign_up

class LoginViewModel(
    private val authApi: AuthApi,
    private val sessionRepository: SessionRepository,
) : ViewModel() {

    private val validator: CredentialsValidator = CredentialsValidator()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _uiEvent = Channel<LoginUiEffect>()
    val uiEvent: Flow<LoginUiEffect> = _uiEvent.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> _uiState.update {
                it.copy(
                    email = action.value,
                    emailError = null,
                    loginError = null,
                )
            }

            is LoginAction.PasswordChanged -> _uiState.update {
                it.copy(
                    password = action.value,
                    passwordError = null,
                    loginError = null,
                )
            }

            LoginAction.SubmitClicked -> submit()
            LoginAction.RememberMeClicked -> emitMessage(UiText.StringResourceId(Res.string.not_implemented_remember_me))
            LoginAction.ForgotPasswordClicked -> emitMessage(UiText.StringResourceId(Res.string.not_implemented_forgot_password))
            LoginAction.SignUpClicked -> emitMessage(UiText.StringResourceId(Res.string.not_implemented_sign_up))
        }
    }

    private fun submit() {
        val currentState = _uiState.value
        if (currentState.isSubmitting) return

        val email = currentState.email.trim()
        val password = currentState.password.trim()

        val validationResult = validator.validate(email, password)

        if (!validationResult.isValid) {
            _uiState.update {
                it.copy(
                    emailError = validationResult.emailError,
                    passwordError = validationResult.passwordError,
                    loginError = null,
                )
            }
            return
        }

        _uiState.update { it.copy(isSubmitting = true, loginError = null) }

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
                    _uiState.update { it.copy(isSubmitting = false) }
                }

                is ApiResult.Failure -> {
                    _uiState.update {
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
