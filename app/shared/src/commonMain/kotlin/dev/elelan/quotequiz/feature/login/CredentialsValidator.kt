package dev.elelan.quotequiz.feature.login

import dev.elelan.quotequiz.core.ui.UiText
import quotequiz.app.shared.generated.resources.Res
import quotequiz.app.shared.generated.resources.login_error_email_required
import quotequiz.app.shared.generated.resources.login_error_password_required

class CredentialsValidator {

    data class ValidationResult(
        val emailError: UiText? = null,
        val passwordError: UiText? = null,
    ) {
        val isValid: Boolean get() = emailError == null && passwordError == null
    }

    fun validate(email: String, password: String): ValidationResult {
        val emailError = if (email.trim().isBlank()) {
            UiText.StringResourceId(Res.string.login_error_email_required)
        } else {
            null
        }

        val passwordError = if (password.isBlank()) {
            UiText.StringResourceId(Res.string.login_error_password_required)
        } else {
            null
        }

        return ValidationResult(
            emailError = emailError,
            passwordError = passwordError,
        )
    }
}