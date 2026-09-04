package xelagurd.socialdating.server.security

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.server.ServerWebExchange
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtException
import reactor.core.publisher.Mono
import xelagurd.socialdating.server.utils.GatewayLocalizedMessages

@Component
class JwtAuthFilter(
    private val jwtUtils: JwtValidationUtils,
    private val securityProperties: GatewaySecurityProperties
) : GlobalFilter, Ordered {

    val logger = KotlinLogging.logger { }
    val antPathMatcher = AntPathMatcher()

    private val messages = GatewayLocalizedMessages()

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val whitelist = securityProperties.whitelist
        val requestPath = request.uri.path

        if (whitelist.any { antPathMatcher.match(it, requestPath) }) {
            return chain.filter(exchange)
        }

        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error { "Missing or invalid Authorization header" }
            return unauthorized(exchange, "error.auth.missingHeader")
        }

        return try {
            val token = authHeader.substring(7)
            val claims = jwtUtils.getClaims(token)

            if (!jwtUtils.isAccessToken(token)) {
                logger.error { "Token is not a valid access token" }
                return unauthorized(exchange, "error.auth.notAccessToken")
            }

            if (!jwtUtils.isAccessTokenValid(token)) {
                logger.error { "Expired access token" }
                return unauthorized(exchange, "error.auth.expiredToken")
            }

            val userId = claims["userId"].toString()
            val role = claims["role"].toString()

            val mutatedRequest = request.mutate()
                .header("X-Auth-UserId", userId)
                .header("X-Auth-Role", role)
                .build()

            val mutatedExchange = exchange.mutate().request(mutatedRequest).build()

            chain.filter(mutatedExchange)
        } catch (_: JwtException) {
            logger.error { "Invalid JWT token" }
            unauthorized(exchange, "error.auth.invalidToken")
        }
    }

    override fun getOrder() = -1

    private fun unauthorized(exchange: ServerWebExchange, messageKey: String): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType(MediaType.TEXT_PLAIN, Charsets.UTF_8)

        val message = messages.get(messageKey, exchange.request.headers)
        val buffer = response.bufferFactory().wrap(message.toByteArray())

        return response.writeWith(Mono.just(buffer))
    }
}
