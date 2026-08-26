package xelagurd.socialdating.client.test

import android.text.TextUtils
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import xelagurd.socialdating.client.data.fake.FakeData

class RegistrationFormDataTest {

    private val registrationFormData = FakeData.registrationFormData

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
}