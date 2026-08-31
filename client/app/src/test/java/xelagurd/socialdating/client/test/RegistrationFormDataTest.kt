package xelagurd.socialdating.client.test

import android.text.TextUtils
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.CITY_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.CITY_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.EMAIL_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.EMAIL_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.NAME_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.NAME_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PASSWORD_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PASSWORD_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.USERNAME_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.USERNAME_LENGTH_MIN
import xelagurd.socialdating.client.ui.form.FormFieldError

class RegistrationFormDataTest {

    private val registrationFormData = FakeData.registrationFormData

    private val ageError = FormFieldError(R.string.error_age, listOf(AGE_MIN, AGE_MAX))

    private fun lengthError(minLength: Int, maxLength: Int) =
        FormFieldError(R.string.error_length, listOf(minLength, maxLength))

    @Before
    fun setup() {
        mockkStatic(TextUtils::class)
        every { TextUtils.isDigitsOnly(any()) } answers {
            firstArg<CharSequence>().let { it.isNotEmpty() && it.all(Char::isDigit) }
        }
    }

    @After
    fun tearDown() {
        unmockkStatic(TextUtils::class)
    }

    @Test
    fun registrationFormData_allData_isValid() {
        assertTrue(registrationFormData.isValid)
    }

    @Test
    fun registrationFormData_emptyData_isNotValid() {
        val emptyFormData = registrationFormData.copy(
            name = "", gender = null, username = "", password = "", repeatedPassword = "",
            email = "", age = "", city = "", purpose = null
        )

        assertFalse(emptyFormData.isValid)
    }

    @Test
    fun registrationFormData_emptyEmail_isValid() {
        assertTrue(registrationFormData.copy(email = "").isValid)
    }

    @Test
    fun registrationFormData_emptyName_isNotValid() {
        assertFalse(registrationFormData.copy(name = "").isValid)
    }

    @Test
    fun registrationFormData_emptyGender_isNotValid() {
        assertFalse(registrationFormData.copy(gender = null).isValid)
    }

    @Test
    fun registrationFormData_emptyUsername_isNotValid() {
        assertFalse(registrationFormData.copy(username = "").isValid)
    }

    @Test
    fun registrationFormData_emptyPassword_isNotValid() {
        assertFalse(registrationFormData.copy(password = "").isValid)
    }

    @Test
    fun registrationFormData_emptyRepeatedPassword_isNotValid() {
        assertFalse(registrationFormData.copy(repeatedPassword = "").isValid)
    }

    @Test
    fun registrationFormData_passwordIsNotEqualToRepeatedPassword_isNotValid() {
        assertFalse(registrationFormData.copy(password = "123", repeatedPassword = "321").isValid)
    }

    @Test
    fun registrationFormData_emptyAge_isNotValid() {
        assertFalse(registrationFormData.copy(age = "").isValid)
    }

    @Test
    fun registrationFormData_notNumberAge_isNotValid() {
        assertFalse(registrationFormData.copy(age = "abcde").isValid)
    }

    @Test
    fun registrationFormData_emptyCity_isNotValid() {
        assertFalse(registrationFormData.copy(city = "").isValid)
    }

    @Test
    fun registrationFormData_emptyPurpose_isNotValid() {
        assertFalse(registrationFormData.copy(purpose = null).isValid)
    }

    @Test
    fun registrationFormData_allData_hasNoErrors() {
        assertNull(registrationFormData.nameError)
        assertNull(registrationFormData.usernameError)
        assertNull(registrationFormData.passwordError)
        assertNull(registrationFormData.repeatedPasswordError)
        assertNull(registrationFormData.emailError)
        assertNull(registrationFormData.ageError)
        assertNull(registrationFormData.cityError)
    }

    @Test
    fun registrationFormData_emptyData_hasNoErrors() {
        val emptyFormData = registrationFormData.copy(
            name = "", username = "", password = "", repeatedPassword = "",
            email = "", age = "", city = ""
        )

        assertNull(emptyFormData.nameError)
        assertNull(emptyFormData.usernameError)
        assertNull(emptyFormData.passwordError)
        assertNull(emptyFormData.repeatedPasswordError)
        assertNull(emptyFormData.emailError)
        assertNull(emptyFormData.ageError)
        assertNull(emptyFormData.cityError)
    }

    @Test
    fun registrationFormData_shortName_hasLengthError() {
        val formData = registrationFormData.copy(name = "a".repeat(NAME_LENGTH_MIN - 1))

        assertEquals(lengthError(NAME_LENGTH_MIN, NAME_LENGTH_MAX), formData.nameError)
    }

    @Test
    fun registrationFormData_longName_hasLengthError() {
        val formData = registrationFormData.copy(name = "a".repeat(NAME_LENGTH_MAX + 1))

        assertEquals(lengthError(NAME_LENGTH_MIN, NAME_LENGTH_MAX), formData.nameError)
    }

    @Test
    fun registrationFormData_longUsername_hasLengthError() {
        val formData = registrationFormData.copy(username = "a".repeat(USERNAME_LENGTH_MAX + 1))

        assertEquals(lengthError(USERNAME_LENGTH_MIN, USERNAME_LENGTH_MAX), formData.usernameError)
    }

    @Test
    fun registrationFormData_usernameWithForbiddenSymbols_hasFormatError() {
        val formData = registrationFormData.copy(username = "user name")

        assertEquals(FormFieldError(R.string.error_username_format), formData.usernameError)
    }

    @Test
    fun registrationFormData_shortPassword_hasLengthError() {
        val formData = registrationFormData.copy(password = "a".repeat(PASSWORD_LENGTH_MIN - 1))

        assertEquals(lengthError(PASSWORD_LENGTH_MIN, PASSWORD_LENGTH_MAX), formData.passwordError)
    }

    @Test
    fun registrationFormData_passwordIsNotEqualToRepeatedPassword_hasRepeatedPasswordError() {
        val formData = registrationFormData.copy(repeatedPassword = "password2")

        assertEquals(FormFieldError(R.string.error_repeated_password), formData.repeatedPasswordError)
    }

    @Test
    fun registrationFormData_incorrectEmail_hasFormatError() {
        val formData = registrationFormData.copy(email = "email1gmail.com")

        assertEquals(FormFieldError(R.string.error_email_format), formData.emailError)
    }

    @Test
    fun registrationFormData_longEmail_hasLengthError() {
        val formData = registrationFormData.copy(email = "a".repeat(EMAIL_LENGTH_MAX) + "@gmail.com")

        assertEquals(lengthError(EMAIL_LENGTH_MIN, EMAIL_LENGTH_MAX), formData.emailError)
    }

    @Test
    fun registrationFormData_notNumberAge_hasAgeError() {
        val formData = registrationFormData.copy(age = "abcde")

        assertEquals(ageError, formData.ageError)
    }

    @Test
    fun registrationFormData_tooSmallAge_hasAgeError() {
        val formData = registrationFormData.copy(age = (AGE_MIN - 1).toString())

        assertEquals(ageError, formData.ageError)
    }

    @Test
    fun registrationFormData_tooBigAge_hasAgeError() {
        val formData = registrationFormData.copy(age = (AGE_MAX + 1).toString())

        assertEquals(ageError, formData.ageError)
    }

    @Test
    fun registrationFormData_shortCity_hasLengthError() {
        val formData = registrationFormData.copy(city = "a".repeat(CITY_LENGTH_MIN - 1))

        assertEquals(lengthError(CITY_LENGTH_MIN, CITY_LENGTH_MAX), formData.cityError)
    }

    @Test
    fun registrationFormData_longCity_hasLengthError() {
        val formData = registrationFormData.copy(city = "a".repeat(CITY_LENGTH_MAX + 1))

        assertEquals(lengthError(CITY_LENGTH_MIN, CITY_LENGTH_MAX), formData.cityError)
    }
}
