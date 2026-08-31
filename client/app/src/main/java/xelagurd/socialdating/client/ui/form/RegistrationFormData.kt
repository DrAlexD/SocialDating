package xelagurd.socialdating.client.ui.form

import xelagurd.socialdating.client.data.model.DefaultDataProperties.CITY_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.CITY_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.NAME_LENGTH_MAX
import xelagurd.socialdating.client.data.model.DefaultDataProperties.NAME_LENGTH_MIN
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidAge
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidEmail
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidPassword
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidText
import xelagurd.socialdating.client.data.model.DefaultDataProperties.isValidUsername
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
        get() = name.isValidText(NAME_LENGTH_MIN, NAME_LENGTH_MAX) && gender != null &&
                username.isValidUsername() && password.isValidPassword() &&
                password == repeatedPassword && (email.isBlank() || email.isValidEmail()) && age.isValidAge() &&
                city.isValidText(CITY_LENGTH_MIN, CITY_LENGTH_MAX) && purpose != null

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

    override fun toString() = "RegistrationFormData(name=$name, gender=$gender, username=$username, email=$email, " +
            "age=$age, city=$city, purpose=$purpose)"
}
