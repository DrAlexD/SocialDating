package xelagurd.socialdating.server.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.transaction.TransactionSystemException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import io.github.oshai.kotlinlogging.KotlinLogging
import xelagurd.socialdating.server.utils.ExceptionUtils.createWrongDataMessage
import xelagurd.socialdating.server.utils.ExceptionUtils.transformNotUniqueDataMessage

@RestControllerAdvice
class UsersExceptionHandler {
    val logger = KotlinLogging.logger { }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): String {
        val message = createWrongDataMessage(ex.bindingResult.fieldErrors.map { it.field to it.defaultMessage })
        logger.error { "Class: ${ex.javaClass.simpleName}, message: $message" }
        return message
    }

    @ExceptionHandler(TransactionSystemException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTransactionSystemException(ex: TransactionSystemException): String {
        val message = "Invalid data (empty or wrong values)"
        logger.error { "Class: ${ex.javaClass.simpleName}, message: $message" }
        return message
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException): String {
        val message = ex.message?.transformNotUniqueDataMessage() ?: "Invalid data (not unique values)"
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