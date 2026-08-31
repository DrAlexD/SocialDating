package xelagurd.socialdating.client.ui.form

import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidPassword
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidUsername
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.ui.form.FormFieldErrors.passwordError
import xelagurd.socialdating.client.ui.form.FormFieldErrors.usernameError

data class LoginFormData(
    val username: String = "",
    val password: String = ""
) : FormData {
    val isValid
        get() = username.isValidUsername() && password.isValidPassword()

    val usernameError
        get() = username.usernameError()

    val passwordError
        get() = password.passwordError()

    fun toLoginDetails() =
        LoginDetails(
            username = username,
            password = password
        )

    override fun toString() = "LoginFormData(username=$username)"
}
