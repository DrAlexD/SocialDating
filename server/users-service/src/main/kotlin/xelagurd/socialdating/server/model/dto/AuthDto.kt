package xelagurd.socialdating.server.model.dto

data class AuthDto(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String
) {
    override fun toString() = "AuthDto(user=$user)"
}
