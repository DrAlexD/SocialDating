package xelagurd.socialdating.server.exception

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.transaction.TransactionSystemException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.github.oshai.kotlinlogging.KotlinLogging
import xelagurd.socialdating.server.utils.ExceptionUtils.createWrongDataMessage
import xelagurd.socialdating.server.utils.ExceptionUtils.getErrorPositionFromStackTrace
import xelagurd.socialdating.server.utils.ExceptionUtils.transformNotUniqueDataMessage
import xelagurd.socialdating.server.utils.ExceptionUtils.transformWrongDataMessage
import xelagurd.socialdating.server.utils.ExceptionUtils.transformWrongInputMessage
import xelagurd.socialdating.server.utils.LocalizedMessages

@RestControllerAdvice
class BaseExceptionHandler {
    val logger = KotlinLogging.logger { }
    private val messages = LocalizedMessages()

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): String {
        val fieldErrors = ex.bindingResult.fieldErrors.map { it.field to it.defaultMessage }
        val globalErrors = ex.bindingResult.globalErrors.map { it.objectName to it.defaultMessage }

        val message = createWrongDataMessage(fieldErrors + globalErrors, messages)
            .ifEmpty { messages.get("error.invalidData.wrongValues") }
        val origin = getErrorPositionFromStackTrace(ex.stackTrace)
        logger.error { "Class: ${ex.javaClass.simpleName}, origin: $origin, message: $message" }
        return message
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): String {
        val message = (ex.cause as? MismatchedInputException)?.transformWrongInputMessage(messages)
            ?: messages.get("error.invalidData.wrongFormat")
        val origin = getErrorPositionFromStackTrace(ex.stackTrace)
        logger.error { "Class: ${ex.javaClass.simpleName}, origin: $origin, message: $message" }
        return message
    }

    @ExceptionHandler(TransactionSystemException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleTransactionSystemException(ex: TransactionSystemException): String {
        val message = messages.get("error.invalidData.emptyOrWrongValues")
        val detailedMessage = ex.message ?: message
        val origin = getErrorPositionFromStackTrace(ex.stackTrace)
        logger.error { "Class: ${ex.javaClass.simpleName}, origin: $origin, message: $detailedMessage" }
        return message
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(ex: DataIntegrityViolationException): ResponseEntity<String> {
        val detailedMessage = ex.mostSpecificCause.message ?: ex.message

        val (status, message) = when (val wrongDataMessage = detailedMessage?.transformWrongDataMessage(messages)) {
            null -> HttpStatus.CONFLICT to (detailedMessage?.transformNotUniqueDataMessage(messages)
                ?: messages.get("error.invalidData.notUniqueValues"))

            else -> HttpStatus.BAD_REQUEST to wrongDataMessage
        }

        val origin = getErrorPositionFromStackTrace(ex.stackTrace)
        logger.error { "Class: ${ex.javaClass.simpleName}, origin: $origin, message: $message" }
        return ResponseEntity.status(status).body(message)
    }

    @ExceptionHandler(AccessDeniedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAccessDeniedException(ex: AccessDeniedException): String {
        val message = when (ex) {
            is ForbiddenDataException -> messages.get(ex.messageKey)
            else -> ex.message ?: messages.get("error.accessDenied")
        }
        val origin = getErrorPositionFromStackTrace(ex.stackTrace)
        logger.error { "Class: ${ex.javaClass.simpleName}, origin: $origin, message: $message" }
        return message
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): String {
        val message = when (ex) {
            is InvalidDataException -> messages.get(ex.messageKey, *ex.messageArgs)
            else -> ex.message ?: messages.get("error.invalidArgument")
        }
        val origin = getErrorPositionFromStackTrace(ex.stackTrace)
        logger.error { "Class: ${ex.javaClass.simpleName}, origin: $origin, message: $message" }
        return message
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleGenericException(ex: Exception): String {
        val message = messages.get("error.unknownServerError")
        val detailedMessage = ex.message ?: message
        val origin = getErrorPositionFromStackTrace(ex.stackTrace)
        logger.error { "Class: ${ex.javaClass.simpleName}, origin: $origin, message: $detailedMessage" }
        return message
    }
}