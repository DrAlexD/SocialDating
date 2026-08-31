package xelagurd.socialdating.client.ui.form

import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidPassword
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidUsername
import xelagurd.socialdating.client.data.model.details.LoginDetails

data class LoginFormData(
    val username: String = "",
    val password: String = ""
) : FormData {
    val isValid
        get() = username.isValidUsername() && password.isValidPassword()

    fun toLoginDetails() =
        LoginDetails(
            username = username,
            password = password
        )

    override fun toString() = "LoginFormData(username=$username)"
}
