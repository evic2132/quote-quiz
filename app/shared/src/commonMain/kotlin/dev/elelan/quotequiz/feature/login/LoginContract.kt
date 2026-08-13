package dev.elelan.quotequiz.feature.login

import androidx.compose.foundation.text.input.TextFieldState
import dev.elelan.quotequiz.core.ui.UiText

data class LoginUiState(
    val email: TextFieldState = TextFieldState(),
    val password: TextFieldState = TextFieldState(),
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val loginError: UiText? = null,
    val isSubmitting: Boolean = false,
)

sealed interface LoginUiEffect {
    data class ShowMessage(val message: UiText) : LoginUiEffect
}

sealed interface LoginAction {
    data object EmailChanged : LoginAction
    data object PasswordChanged : LoginAction
    data object SubmitClicked : LoginAction
    data object RememberMeClicked : LoginAction
    data object ForgotPasswordClicked : LoginAction
    data object SignUpClicked : LoginAction
}
