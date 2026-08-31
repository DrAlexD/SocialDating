package xelagurd.socialdating.client.test

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PASSWORD_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PASSWORD_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.USERNAME_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.USERNAME_LENGTH_MIN
import xelagurd.socialdating.client.ui.form.FormFieldError

class LoginFormDataTest {

    private val loginFormData = FakeData.loginFormData

    private val usernameLengthError =
        FormFieldError(R.string.error_length, listOf(USERNAME_LENGTH_MIN, USERNAME_LENGTH_MAX))
    private val passwordLengthError =
        FormFieldError(R.string.error_length, listOf(PASSWORD_LENGTH_MIN, PASSWORD_LENGTH_MAX))

    @Test
    fun loginFormData_allData_isValid() {
        assertTrue(loginFormData.isValid)
    }

    @Test
    fun loginFormData_emptyData_isNotValid() {
        assertFalse(loginFormData.copy(username = "", password = "").isValid)
    }

    @Test
    fun loginFormData_emptyUsername_isNotValid() {
        assertFalse(loginFormData.copy(username = "").isValid)
    }

    @Test
    fun loginFormData_blankUsername_isNotValid() {
        assertFalse(loginFormData.copy(username = " ").isValid)
    }

    @Test
    fun loginFormData_emptyPassword_isNotValid() {
        assertFalse(loginFormData.copy(password = "").isValid)
    }

    @Test
    fun loginFormData_blankPassword_isNotValid() {
        assertFalse(loginFormData.copy(password = " ").isValid)
    }

    @Test
    fun loginFormData_allData_hasNoErrors() {
        assertNull(loginFormData.usernameError)
        assertNull(loginFormData.passwordError)
    }

    @Test
    fun loginFormData_emptyData_hasNoErrors() {
        val emptyFormData = loginFormData.copy(username = "", password = "")

        assertNull(emptyFormData.usernameError)
        assertNull(emptyFormData.passwordError)
    }

    @Test
    fun loginFormData_shortUsername_hasLengthError() {
        val formData = loginFormData.copy(username = "a".repeat(USERNAME_LENGTH_MIN - 1))

        assertEquals(usernameLengthError, formData.usernameError)
    }

    @Test
    fun loginFormData_longUsername_hasLengthError() {
        val formData = loginFormData.copy(username = "a".repeat(USERNAME_LENGTH_MAX + 1))

        assertEquals(usernameLengthError, formData.usernameError)
    }

    @Test
    fun loginFormData_usernameWithForbiddenSymbols_hasFormatError() {
        val formData = loginFormData.copy(username = "user name")

        assertEquals(FormFieldError(R.string.error_username_format), formData.usernameError)
    }

    @Test
    fun loginFormData_shortPassword_hasLengthError() {
        val formData = loginFormData.copy(password = "a".repeat(PASSWORD_LENGTH_MIN - 1))

        assertEquals(passwordLengthError, formData.passwordError)
    }

    @Test
    fun loginFormData_longPassword_hasLengthError() {
        val formData = loginFormData.copy(password = "a".repeat(PASSWORD_LENGTH_MAX + 1))

        assertEquals(passwordLengthError, formData.passwordError)
    }
}
