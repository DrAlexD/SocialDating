package xelagurd.socialdating.server.test

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.access.AccessDeniedException
import org.springframework.transaction.TransactionSystemException
import org.springframework.validation.BindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
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
    fun handleDataIntegrityViolationException_uniqueConstraintMessage_returnsTransformedMessage() {
        val message =
            "Unique index violation: \"PUBLIC.UK_X ON PUBLIC.CATEGORIES(NAME NULLS FIRST) VALUES ( 1, 'Sport' )\""

        assertEquals(
            "Category with 'Sport' name already exists",
            handler.handleDataIntegrityViolationException(DataIntegrityViolationException(message))
        )
    }

    @Test
    fun handleDataIntegrityViolationException_otherMessage_returnsDefaultMessage() {
        assertEquals(
            "Invalid data (not unique values)",
            handler.handleDataIntegrityViolationException(DataIntegrityViolationException("some other error"))
        )
    }

    @Test
    fun handleDataIntegrityViolationException_nullMessage_returnsDefaultMessage() {
        val ex = mockk<DataIntegrityViolationException>(relaxed = true) {
            every { message } returns null
        }

        assertEquals("Invalid data (not unique values)", handler.handleDataIntegrityViolationException(ex))
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
