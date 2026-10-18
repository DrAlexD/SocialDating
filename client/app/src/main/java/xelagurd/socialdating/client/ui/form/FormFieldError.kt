package xelagurd.socialdating.client.ui.form

import androidx.annotation.StringRes
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.DATE_DIGITS_NUMBER
import xelagurd.socialdating.client.data.model.DefaultDataProperties.EMAIL_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.EMAIL_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PASSWORD_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.PASSWORD_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.USERNAME_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.USERNAME_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidBirthDate
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidDate
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidEmailFormat
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidPassword
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidText
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidUsernameFormat

data class FormFieldError(
    @param:StringRes val messageRes: Int,
    val formatArgs: List<Any> = listOf()
)

object FormFieldErrors {

    fun String.textError(minLength: Int, maxLength: Int) = when {
        isEmpty() -> null
        !isValidText(minLength, maxLength) -> lengthError(minLength, maxLength)
        else -> null
    }

    fun String.usernameError() = when {
        isEmpty() -> null
        !isValidText(USERNAME_LENGTH_MIN, USERNAME_LENGTH_MAX) ->
            lengthError(USERNAME_LENGTH_MIN, USERNAME_LENGTH_MAX)

        !isValidUsernameFormat() -> FormFieldError(R.string.error_username_format)
        else -> null
    }

    fun String.passwordError() = when {
        isEmpty() -> null
        !isValidPassword() -> lengthError(PASSWORD_LENGTH_MIN, PASSWORD_LENGTH_MAX)
        else -> null
    }

    fun String.repeatedPasswordError(password: String) = when {
        isEmpty() -> null
        this != password -> FormFieldError(R.string.error_repeated_password)
        else -> null
    }

    fun String.emailError() = when {
        isEmpty() -> null
        !isValidEmailFormat() -> FormFieldError(R.string.error_email_format)
        !isValidText(EMAIL_LENGTH_MIN, EMAIL_LENGTH_MAX) ->
            lengthError(EMAIL_LENGTH_MIN, EMAIL_LENGTH_MAX)

        else -> null
    }

    fun String.birthDateError() = when {
        // the date is checked only after it is fully entered
        length < DATE_DIGITS_NUMBER -> null
        !isValidDate() -> FormFieldError(R.string.error_date)
        !isValidBirthDate() -> FormFieldError(R.string.error_age, listOf(AGE_MIN, AGE_MAX))
        else -> null
    }

    fun String.integerError() = when {
        isEmpty() -> null
        toIntOrNull() == null -> FormFieldError(R.string.error_integer)
        else -> null
    }

    fun String.rangeMaxError(min: String) = when {
        isEmpty() && min.isEmpty() -> FormFieldError(R.string.error_empty_range)
        isLessThan(min) -> FormFieldError(R.string.error_less_than_min)
        else -> integerError()
    }

    private fun String.isLessThan(other: String): Boolean {
        val value = toIntOrNull() ?: return false
        val otherValue = other.toIntOrNull() ?: return false

        return value < otherValue
    }

    private fun lengthError(minLength: Int, maxLength: Int) =
        FormFieldError(R.string.error_length, listOf(minLength, maxLength))
}
