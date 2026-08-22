package xelagurd.socialdating.server.test

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.server.ServerWebExchange
import io.jsonwebtoken.Claims
import io.jsonwebtoken.MalformedJwtException
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import xelagurd.socialdating.server.security.GatewaySecurityProperties
import xelagurd.socialdating.server.security.JwtAuthFilter
import xelagurd.socialdating.server.security.JwtValidationUtils

class JwtAuthFilterUnitTest {

    private val jwtUtils = mockk<JwtValidationUtils>()
    private val securityProperties = GatewaySecurityProperties(whitelist = listOf("/api/v1/users/auth/**"))
    private val jwtAuthFilter = JwtAuthFilter(jwtUtils, securityProperties)

    private val token = "token"

    private fun exchange(path: String, authHeader: String? = null): MockServerWebExchange {
        var builder = MockServerHttpRequest.get(path)
        if (authHeader != null) {
            builder = builder.header(HttpHeaders.AUTHORIZATION, authHeader)
        }
        return MockServerWebExchange.from(builder.build())
    }

    @Test
    fun filter_whitelistedPath_passesThrough() {
        val exchange = exchange("/api/v1/users/auth/login")
        val chain = mockk<GatewayFilterChain>()
        every { chain.filter(any()) } returns Mono.empty()

        jwtAuthFilter.filter(exchange, chain).block()

        verify(exactly = 1) { chain.filter(exchange) }
        confirmVerified(jwtUtils)
    }

    @Test
    fun filter_missingAuthHeader_unauthorized() {
        val exchange = exchange("/api/v1/categories")
        val chain = mockk<GatewayFilterChain>()

        jwtAuthFilter.filter(exchange, chain).block()

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.response.statusCode)
        assertEquals("Missing or invalid Authorization header", exchange.response.bodyAsString.block())
        verify(exactly = 0) { chain.filter(any()) }
        confirmVerified(jwtUtils)
    }

    @Test
    fun filter_nonBearerAuthHeader_unauthorized() {
        val exchange = exchange("/api/v1/categories", "Basic credentials")
        val chain = mockk<GatewayFilterChain>()

        jwtAuthFilter.filter(exchange, chain).block()

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.response.statusCode)
        assertEquals("Missing or invalid Authorization header", exchange.response.bodyAsString.block())
        verify(exactly = 0) { chain.filter(any()) }
        confirmVerified(jwtUtils)
    }

    @Test
    fun filter_invalidJwt_unauthorized() {
        every { jwtUtils.getClaims(token) } throws MalformedJwtException("invalid")
        val exchange = exchange("/api/v1/categories", "Bearer $token")
        val chain = mockk<GatewayFilterChain>()

        jwtAuthFilter.filter(exchange, chain).block()

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.response.statusCode)
        assertEquals("Invalid JWT token", exchange.response.bodyAsString.block())
        verify(exactly = 0) { chain.filter(any()) }
    }

    @Test
    fun filter_notAccessToken_unauthorized() {
        every { jwtUtils.getClaims(token) } returns mockk(relaxed = true)
        every { jwtUtils.isAccessToken(token) } returns false
        val exchange = exchange("/api/v1/categories", "Bearer $token")
        val chain = mockk<GatewayFilterChain>()

        jwtAuthFilter.filter(exchange, chain).block()

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.response.statusCode)
        assertEquals("Token is not a valid access token", exchange.response.bodyAsString.block())
        verify(exactly = 0) { chain.filter(any()) }
    }

    @Test
    fun filter_expiredAccessToken_unauthorized() {
        every { jwtUtils.getClaims(token) } returns mockk(relaxed = true)
        every { jwtUtils.isAccessToken(token) } returns true
        every { jwtUtils.isAccessTokenValid(token) } returns false
        val exchange = exchange("/api/v1/categories", "Bearer $token")
        val chain = mockk<GatewayFilterChain>()

        jwtAuthFilter.filter(exchange, chain).block()

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.response.statusCode)
        assertEquals("Expired access token", exchange.response.bodyAsString.block())
        verify(exactly = 0) { chain.filter(any()) }
    }

    @Test
    fun filter_validAccessToken_addsAuthHeadersAndForwards() {
        val claims = mockk<Claims> {
            every { this@mockk["userId"] } returns 42
            every { this@mockk["role"] } returns "ADMIN"
        }
        every { jwtUtils.getClaims(token) } returns claims
        every { jwtUtils.isAccessToken(token) } returns true
        every { jwtUtils.isAccessTokenValid(token) } returns true

        val exchange = exchange("/api/v1/categories", "Bearer $token")
        val chain = mockk<GatewayFilterChain>()
        val forwarded = slot<ServerWebExchange>()
        every { chain.filter(capture(forwarded)) } returns Mono.empty()

        jwtAuthFilter.filter(exchange, chain).block()

        val forwardedRequest = forwarded.captured.request
        assertEquals("42", forwardedRequest.headers.getFirst("X-Auth-UserId"))
        assertEquals("ADMIN", forwardedRequest.headers.getFirst("X-Auth-Role"))
        verify(exactly = 1) { chain.filter(any()) }
    }
}
