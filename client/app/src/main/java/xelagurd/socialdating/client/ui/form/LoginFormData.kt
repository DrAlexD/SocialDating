package xelagurd.socialdating.client.ui.form

import xelagurd.socialdating.client.data.model.details.LoginDetails

data class LoginFormData(
    val username: String = "",
    val password: String = ""
) : FormData {
    val isValid
        get() = username.isNotBlank() && password.isNotBlank()

    fun toLoginDetails() =
        LoginDetails(
            username = username,
            password = password
        )
}