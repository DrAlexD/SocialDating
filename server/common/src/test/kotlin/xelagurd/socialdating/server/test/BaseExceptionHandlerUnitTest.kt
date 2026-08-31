package xelagurd.socialdating.server.test

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.transaction.TransactionSystemException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.exception.BaseExceptionHandler

class BaseExceptionHandlerUnitTest {

    private val handler = BaseExceptionHandler()

    @Test
    fun handleMethodArgumentNotValidException_fieldErrors_buildsSortedTitlecasedMessage() {
        val bindingResult = mockk<BindingResult> {
            every { fieldErrors } returns listOf(
                FieldError("object", "username", "must not be blank"),
                FieldError("object", "age", "must be greater than 0")
            )
            every { globalErrors } returns emptyList()
        }
        val ex = mockk<MethodArgumentNotValidException>(relaxed = true) {
            every { this@mockk.bindingResult } returns bindingResult
        }

        assertEquals(
            "'Age' must be greater than 0; 'Username' must not be blank",
            handler.handleMethodArgumentNotValidException(ex)
        )
    }

    @Test
    fun handleMethodArgumentNotValidException_noErrors_returnsGenericMessage() {
        val bindingResult = mockk<BindingResult> {
            every { fieldErrors } returns emptyList()
            every { globalErrors } returns emptyList()
        }
        val ex = mockk<MethodArgumentNotValidException>(relaxed = true) {
            every { this@mockk.bindingResult } returns bindingResult
        }

        assertEquals("Invalid data (wrong values)", handler.handleMethodArgumentNotValidException(ex))
    }

    @Test
    fun handleMethodArgumentNotValidException_globalError_returnsObjectMessage() {
        val bindingResult = mockk<BindingResult> {
            every { fieldErrors } returns emptyList()
            every { globalErrors } returns listOf(ObjectError("registrationDetails", "passwords must match"))
        }
        val ex = mockk<MethodArgumentNotValidException>(relaxed = true) {
            every { this@mockk.bindingResult } returns bindingResult
        }

        assertEquals("'RegistrationDetails' passwords must match", handler.handleMethodArgumentNotValidException(ex))
    }

    @Test
    fun handleHttpMessageNotReadableException_wrongEnumValue_returnsFieldMessage() {
        val cause = mockk<InvalidFormatException>(relaxed = true) {
            every { path } returns listOf(JsonMappingException.Reference(null, "gender"))
        }
        val ex = mockk<HttpMessageNotReadableException>(relaxed = true) {
            every { this@mockk.cause } returns cause
        }

        assertEquals("'Gender' has wrong value", handler.handleHttpMessageNotReadableException(ex))
    }

    @Test
    fun handleHttpMessageNotReadableException_nullValue_returnsNotNullMessage() {
        val cause = mockk<MismatchedInputException>(relaxed = true) {
            every { path } returns listOf(JsonMappingException.Reference(null, "name"))
        }
        val ex = mockk<HttpMessageNotReadableException>(relaxed = true) {
            every { this@mockk.cause } returns cause
        }

        assertEquals("'Name' must not be null", handler.handleHttpMessageNotReadableException(ex))
    }

    @Test
    fun handleHttpMessageNotReadableException_unknownCause_returnsGenericMessage() {
        val ex = mockk<HttpMessageNotReadableException>(relaxed = true) {
            every { cause } returns null
        }

        assertEquals("Invalid data (wrong format)", handler.handleHttpMessageNotReadableException(ex))
    }

    @Test
    fun handleTransactionSystemException_withMessage_returnsGenericMessage() {
        assertEquals(
            "Invalid data (empty or wrong values)",
            handler.handleTransactionSystemException(TransactionSystemException("constraint violation"))
        )
    }

    @Test
    fun handleTransactionSystemException_nullMessage_returnsGenericMessage() {
        val ex = mockk<TransactionSystemException>(relaxed = true) {
            every { message } returns null
        }

        assertEquals("Invalid data (empty or wrong values)", handler.handleTransactionSystemException(ex))
    }

    @Test
    fun handleDataIntegrityViolationException_checkConstraintMessage_returnsBadRequestWithField() {
        val message = "new row for relation \"users\" violates check constraint \"users_name_check\""
        val response = handler.handleDataIntegrityViolationException(DataIntegrityViolationException(message))

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("'Name' has wrong value", response.body)
    }

    @Test
    fun handleDataIntegrityViolationException_notNullMessage_returnsBadRequestWithField() {
        val message = "null value in column \"city\" of relation \"users\" violates not-null constraint"
        val response = handler.handleDataIntegrityViolationException(DataIntegrityViolationException(message))

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("'City' must not be null", response.body)
    }

    @Test
    fun handleDataIntegrityViolationException_uniqueConstraintMessage_returnsConflictWithField() {
        val message = "duplicate key value violates unique constraint \"uk_name\""
        val response = handler.handleDataIntegrityViolationException(DataIntegrityViolationException(message))

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("'Name' already exists", response.body)
    }

    @Test
    fun handleDataIntegrityViolationException_otherMessage_returnsDefaultMessage() {
        assertEquals(
            "Invalid data (not unique values)",
            handler.handleDataIntegrityViolationException(DataIntegrityViolationException("some other error")).body
        )
    }

    @Test
    fun handleDataIntegrityViolationException_nullMessage_returnsDefaultMessage() {
        val ex = mockk<DataIntegrityViolationException>(relaxed = true) {
            every { message } returns null
        }

        assertEquals("Invalid data (not unique values)", handler.handleDataIntegrityViolationException(ex).body)
    }

    @Test
    fun handleAccessDeniedException_withMessage_returnsMessage() {
        assertEquals("no access", handler.handleAccessDeniedException(AccessDeniedException("no access")))
    }

    @Test
    fun handleAccessDeniedException_nullMessage_returnsDefault() {
        val ex = mockk<AccessDeniedException>(relaxed = true) {
            every { message } returns null
        }

        assertEquals("Access denied", handler.handleAccessDeniedException(ex))
    }

    @Test
    fun handleIllegalArgumentException_withMessage_returnsMessage() {
        assertEquals("bad arg", handler.handleIllegalArgumentException(IllegalArgumentException("bad arg")))
    }

    @Test
    fun handleIllegalArgumentException_nullMessage_returnsDefault() {
        assertEquals("Invalid argument", handler.handleIllegalArgumentException(IllegalArgumentException()))
    }

    @Test
    fun handleGenericException_withMessage_returnsUnknownError() {
        assertEquals("Unknown server error", handler.handleGenericException(Exception("boom")))
    }

    @Test
    fun handleGenericException_nullMessage_returnsUnknownError() {
        assertEquals("Unknown server error", handler.handleGenericException(Exception()))
    }
}
