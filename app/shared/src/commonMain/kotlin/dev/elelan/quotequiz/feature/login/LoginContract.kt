package dev.elelan.quotequiz.feature.login

import dev.elelan.quotequiz.core.ui.UiText

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val loginError: UiText? = null,
    val isSubmitting: Boolean = false,
)

sealed interface LoginUiEffect {
    data class ShowMessage(val message: UiText) : LoginUiEffect
}

sealed interface LoginAction {
    data class EmailChanged(val value: String) : LoginAction
    data class PasswordChanged(val value: String) : LoginAction
    data object SubmitClicked : LoginAction
    data object RememberMeClicked : LoginAction
    data object ForgotPasswordClicked : LoginAction
    data object SignUpClicked : LoginAction
}
