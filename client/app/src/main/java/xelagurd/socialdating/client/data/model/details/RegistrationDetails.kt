package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose

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
) : FormDetails {
    val isValid
        get() = name.isNotBlank() && gender != null && username.isNotBlank() && password.isNotBlank() &&
                repeatedPassword.isNotBlank() && password == repeatedPassword && age.isNotBlank() &&
                city.isNotBlank() && purpose != null

    fun toLoginDetails() = LoginDetails(username, password)
}