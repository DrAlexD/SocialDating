package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.enums.Gender
import xelagurd.socialdating.client.data.model.enums.Purpose

@Serializable
data class RegistrationDetails(
    val name: String,
    val gender: Gender,
    val username: String,
    val password: String,
    val telegram: String,
    // ISO 8601 date, as YYYY-MM-DD
    val birthDate: String,
    val city: String,
    val purpose: Purpose
) {
    override fun toString() = "RegistrationDetails(name=$name, gender=$gender, username=$username, telegram=$telegram, " +
            "birthDate=$birthDate, city=$city, purpose=$purpose)"
}