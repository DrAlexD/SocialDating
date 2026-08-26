package xelagurd.socialdating.server.test

import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.security.HeaderAuthFilter

class HeaderAuthFilterUnitTest {

    private val headerAuthFilter = HeaderAuthFilter()

    private fun request(userId: String? = null, role: String? = null) =
        MockHttpServletRequest().apply {
            if (userId != null) addHeader("X-Auth-UserId", userId)
            if (role != null) addHeader("X-Auth-Role", role)
        }

    @AfterEach
    fun clearContext() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun doFilter_bothHeadersPresent_setsAuthenticationAndContinues() {
        val request = request(userId = "7", role = "ADMIN")
        val filterChain = MockFilterChain()

        headerAuthFilter.doFilter(request, MockHttpServletResponse(), filterChain)

        val authentication = SecurityContextHolder.getContext().authentication
        assertEquals(7, authentication.principal)
        assertEquals("ROLE_ADMIN", authentication.authorities.first().authority)
        assertEquals(request, filterChain.request)
    }

    @Test
    fun doFilter_missingUserId_doesNotSetAuthentication() {
        val request = request(role = "ADMIN")
        val filterChain = MockFilterChain()

        headerAuthFilter.doFilter(request, MockHttpServletResponse(), filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
        assertEquals(request, filterChain.request)
    }

    @Test
    fun doFilter_missingRole_doesNotSetAuthentication() {
        val request = request(userId = "7")
        val filterChain = MockFilterChain()

        headerAuthFilter.doFilter(request, MockHttpServletResponse(), filterChain)

        assertNull(SecurityContextHolder.getContext().authentication)
        assertEquals(request, filterChain.request)
    }
}
