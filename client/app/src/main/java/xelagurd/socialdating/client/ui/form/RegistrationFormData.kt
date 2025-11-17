package xelagurd.socialdating.client.ui.form

import androidx.core.text.isDigitsOnly
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose

data class RegistrationFormData(
    val name: String = "",
    val gender: Gender? = null,
    val username: String = "",
    val password: String = "",
    val repeatedPassword: String = "",
    val email: String = "",
    val age: String = "",
    val city: String = "",
    val purpose: Purpose? = null
) : FormData {
    val isValid
        get() = name.isNotBlank() && gender != null && username.isNotBlank() && password.isNotBlank() &&
                repeatedPassword.isNotBlank() && password == repeatedPassword && age.isNotBlank() &&
                age.isDigitsOnly() && city.isNotBlank() && purpose != null

    fun toLoginFormData() =
        LoginFormData(
            username = username,
            password = password
        )

    fun toRegistrationDetails() =
        RegistrationDetails(
            name = name,
            gender = gender!!,
            username = username,
            password = password,
            email = email.takeIf { it.isNotBlank() },
            age = age.toInt(),
            city = city,
            purpose = purpose!!
        )
}