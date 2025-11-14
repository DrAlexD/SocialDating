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
    val email: String?,
    val age: Int,
    val city: String,
    val purpose: Purpose
)