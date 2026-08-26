package xelagurd.socialdating.client.test

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import xelagurd.socialdating.client.data.fake.FakeData

class LoginFormDataTest {

    private val loginFormData = FakeData.loginFormData

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
}