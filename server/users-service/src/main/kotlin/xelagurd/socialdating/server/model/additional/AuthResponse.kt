package xelagurd.socialdating.server.model.additional

import xelagurd.socialdating.server.model.User

data class AuthResponse(
    val user: User,
    val accessToken: String,
    val refreshToken: String
)