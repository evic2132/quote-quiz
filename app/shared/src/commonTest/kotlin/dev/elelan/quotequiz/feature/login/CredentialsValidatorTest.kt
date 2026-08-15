package dev.elelan.quotequiz.feature.login

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CredentialsValidatorTest {

    private val validator = CredentialsValidator()

    @Test
    fun `when email and password are valid returns isValid true`() {
        val result = validator.validate("test@example.com", "password123")

        assertTrue(result.isValid)
        assertNull(result.emailError)
        assertNull(result.passwordError)
    }

    @Test
    fun `when email is blank returns email required error`() {
        val result = validator.validate("   ", "password123")

        assertFalse(result.isValid)
        assertNotNull(result.emailError)
        assertNull(result.passwordError)
    }

    @Test
    fun `when password is blank returns password required error`() {
        val result = validator.validate("test@example.com", "")

        assertFalse(result.isValid)
        assertNull(result.emailError)
        assertNotNull(result.passwordError)
    }
}
