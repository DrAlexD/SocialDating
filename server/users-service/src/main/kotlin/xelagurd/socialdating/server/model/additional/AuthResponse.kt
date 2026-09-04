package xelagurd.socialdating.server.model.additional

data class AuthResponse(
    val user: UserResponse,
    val accessToken: String,
    val refreshToken: String
) {
    override fun toString() = "AuthResponse(user=$user)"
}
