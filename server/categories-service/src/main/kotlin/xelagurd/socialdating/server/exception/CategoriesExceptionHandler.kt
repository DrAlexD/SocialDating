package xelagurd.socialdating.server.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.transaction.TransactionSystemException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import io.github.oshai.kotlinlogging.KotlinLogging

@RestControllerAdvice
class CategoriesExceptionHandler {
    val logger = KotlinLogging.logger { }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): String {
        val message = ex.message
        logger.error { "Class: ${ex.javaClass.simpleName}, message: $message" }
        return message
    }

    @ExceptionHandler(TransactionSystemException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTransactionSystemException(ex: TransactionSystemException): String {
        val message = ex.message ?: "Invalid data (empty, wrong values)"
        logger.error { "Class: ${ex.javaClass.simpleName}, message: $message" }
        return message
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException): String {
        val message = ex.message ?: "Invalid data (not unique values)"
        logger.error { "Class: ${ex.javaClass.simpleName}, message: $message" }
        return message
    }

    @ExceptionHandler(NoDataFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoDataFoundException(ex: NoDataFoundException): String {
        val message = ex.message ?: "No data found"
        logger.error { "Class: ${ex.javaClass.simpleName}, message: $message" }
        return message
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception): String {
        val message = ex.message ?: "Unknown server error"
        logger.error { "Class: ${ex.javaClass.simpleName}, message: $message" }
        return message
    }
}