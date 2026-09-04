package xelagurd.socialdating.server.exception

import org.springframework.security.authentication.CredentialsExpiredException

class ExpiredTokenException : CredentialsExpiredException("Expired refresh token"), LocalizedAuthException {
    override val messageKey = "error.token.expiredRefreshToken"
}
