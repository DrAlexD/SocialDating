package xelagurd.socialdating.server.test

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.utils.SecurityUtils

class SecurityUtilsUnitTest {

    private fun authenticateAs(userId: Int) {
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(userId, null, emptyList())
    }

    @AfterEach
    fun clearContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun checkCurrentUserAuth_sameUser_doesNotThrow() {
        authenticateAs(5)

        assertDoesNotThrow { SecurityUtils.checkCurrentUserAuth(5) }
    }

    @Test
    fun checkCurrentUserAuth_differentUser_throwsAccessDenied() {
        authenticateAs(5)

        assertThrows(AccessDeniedException::class.java) {
            SecurityUtils.checkCurrentUserAuth(10)
        }
    }
}
