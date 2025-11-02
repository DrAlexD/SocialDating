package xelagurd.socialdating.client.data.model.details

import kotlinx.serialization.Serializable

@Serializable
data class LoginDetails(
    val username: String,
    val password: String
)