package xelagurd.socialdating.server.test

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import io.jsonwebtoken.JwtException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.exception.AuthExceptionHandler

class AuthExceptionHandlerUnitTest {

    private val authExceptionHandler = AuthExceptionHandler()

    @Test
    fun handleAuthenticationException_withMessage_returnsMessage() {
        val exception = BadCredentialsException("Bad credentials")

        assertEquals("Bad credentials", authExceptionHandler.handleAuthenticationException(exception))
    }

    @Test
    fun handleAuthenticationException_nullMessage_returnsDefault() {
        val exception = object : AuthenticationException(null as String?) {}

        assertEquals("Unauthorized", authExceptionHandler.handleAuthenticationException(exception))
    }

    @Test
    fun handleJwtException_withMessage_hidesLibraryMessage() {
        val exception = JwtException("JWT expired 2 minutes ago")

        assertEquals("Invalid JWT token", authExceptionHandler.handleJwtException(exception))
    }

    @Test
    fun handleJwtException_nullMessage_returnsDefault() {
        val exception = JwtException(null as String?)

        assertEquals("Invalid JWT token", authExceptionHandler.handleJwtException(exception))
    }
}
