package xelagurd.socialdating.server.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class StatementsExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): String {
        return "Invalid data"
    }

    @ExceptionHandler(NoDataFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoDataFoundException(ex: NoDataFoundException): String {
        return ex.message ?: "No data found"
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception): String {
        return ex.message?.let { "Server error: $it" } ?: "Unknown server error"
    }
}