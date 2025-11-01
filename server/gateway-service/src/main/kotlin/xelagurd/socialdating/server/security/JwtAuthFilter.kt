package xelagurd.socialdating.server.security

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtException
import reactor.core.publisher.Mono

@Component
class JwtAuthFilter(
    private val jwtUtils: JwtValidationUtils,
    private val securityProperties: GatewaySecurityProperties
) : GlobalFilter, Ordered {

    val logger = KotlinLogging.logger { }

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val whitelist = securityProperties.whitelist

        if (whitelist.any { request.uri.path.startsWith(it.removeSuffix("/**")) }) {
            return chain.filter(exchange)
        }

        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error { "Missing or invalid Authorization header" }
            return unauthorized(exchange, "Missing or invalid Authorization header")
        }

        return try {
            val token = authHeader.substring(7)
            val claims = jwtUtils.getClaims(token)

            if (!jwtUtils.isAccessToken(token)) {
                logger.error { "Token is not a valid access token" }
                return unauthorized(exchange, "Token is not a valid access token")
            }

            if (!jwtUtils.isAccessTokenValid(token)) {
                logger.error { "Expired access token" }
                return unauthorized(exchange, "Expired access token")
            }

            val username = claims.subject
            val role = claims["role"].toString()

            val mutatedRequest = request.mutate()
                .header("X-Auth-Username", username)
                .header("X-Auth-Role", role)
                .build()

            val mutatedExchange = exchange.mutate().request(mutatedRequest).build()

            chain.filter(mutatedExchange)
        } catch (_: JwtException) {
            logger.error { "Invalid JWT token" }
            unauthorized(exchange, "Invalid JWT token")
        }
    }

    override fun getOrder(): Int = -1

    private fun unauthorized(exchange: ServerWebExchange, message: String): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED

        val buffer = response.bufferFactory().wrap(message.toByteArray())

        return response.writeWith(Mono.just(buffer))
    }
}
