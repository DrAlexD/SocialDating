package xelagurd.socialdating.server.exception

import org.springframework.security.authentication.BadCredentialsException

class InvalidTokenException : BadCredentialsException("Token is not a valid refresh token"), LocalizedAuthException {
    override val messageKey = "error.token.notRefreshToken"
}
