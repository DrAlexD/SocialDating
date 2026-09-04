package xelagurd.socialdating.server.exception

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtException
import xelagurd.socialdating.server.utils.ExceptionUtils.getErrorPositionFromStackTrace
import xelagurd.socialdating.server.utils.LocalizedMessages

@RestControllerAdvice
class AuthExceptionHandler {
    val logger = KotlinLogging.logger { }
    private val messages = LocalizedMessages()

    @ExceptionHandler(AuthenticationException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleAuthenticationException(ex: AuthenticationException): String {
        val message = when (ex) {
            is LocalizedAuthException -> messages.get(ex.messageKey)
            else -> ex.message ?: messages.get("error.unauthorized")
        }
        val detailedMessage = ex.message ?: message
        val origin = getErrorPositionFromStackTrace(ex.stackTrace)
        logger.error { "Class: ${ex.javaClass.simpleName}, origin: $origin, message: $detailedMessage" }
        return message
    }

    @ExceptionHandler(JwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleJwtException(ex: JwtException): String {
        val message = messages.get("error.invalidToken")
        val detailedMessage = ex.message ?: message
        val origin = getErrorPositionFromStackTrace(ex.stackTrace)
        logger.error { "Class: ${ex.javaClass.simpleName}, origin: $origin, message: $detailedMessage" }
        return message
    }
}