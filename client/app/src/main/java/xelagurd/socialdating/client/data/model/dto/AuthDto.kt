package xelagurd.socialdating.client.data.model.dto

import kotlinx.serialization.Serializable
import xelagurd.socialdating.client.data.model.User

@Serializable
data class AuthDto(
    val user: User,
    val accessToken: String,
    val refreshToken: String
) {
    override fun toString() = "AuthDto(user=$user)"
}