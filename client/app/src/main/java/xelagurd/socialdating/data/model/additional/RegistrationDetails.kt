package xelagurd.socialdating.data.model.additional

import kotlinx.serialization.Serializable
import xelagurd.socialdating.data.model.enums.Gender
import xelagurd.socialdating.data.model.enums.Purpose

@Serializable
data class RegistrationDetails(
    val name: String = "",
    val gender: Gender? = null,
    val username: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val email: String = "",
    val age: String = "",
    val city: String = "",
    val purpose: Purpose? = null
) {
    val isValid
        get() = name.isNotBlank() && gender != null && username.isNotBlank() && password.isNotBlank() &&
                repeatedPassword.isNotBlank() && password == repeatedPassword && age.isNotBlank() &&
                city.isNotBlank() && purpose != null
}