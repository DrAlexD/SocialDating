package xelagurd.socialdating.data.model.additional

import kotlinx.serialization.Serializable

@Serializable
data class LoginDetails(
    val username: String = "",
    val password: String = ""
) {
    val isValid
        get() = username.isNotBlank() && password.isNotBlank()
}