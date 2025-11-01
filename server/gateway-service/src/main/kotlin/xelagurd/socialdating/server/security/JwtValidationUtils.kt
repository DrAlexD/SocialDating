package xelagurd.socialdating.server.security

import java.util.Base64
import java.util.Date
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys

@Component
class JwtValidationUtils(
    private val env: Environment
) {

    private val key by lazy {
        val secret = env.getProperty("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET is not set")
        Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))
    }

    fun isAccessToken(accessToken: String): Boolean {
        val claims = getClaims(accessToken)

        return claims["type"] == "access" && claims.subject != null && claims["role"] != null
    }

    fun isAccessTokenValid(accessToken: String): Boolean {
        val claims = getClaims(accessToken)
        val isTokenExpired = claims.expiration.before(Date())

        return !isTokenExpired
    }

    fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}