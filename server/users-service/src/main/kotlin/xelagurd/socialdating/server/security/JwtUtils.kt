package xelagurd.socialdating.server.security

import java.util.Base64
import java.util.Date
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.Role

@Component
class JwtUtils(
    private val env: Environment
) {

    private val key by lazy {
        val secret = env.getProperty("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET is not set")
        Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))
    }

    fun generateAccessToken(username: String, role: Role): String {
        return Jwts.builder()
            .subject(username)
            .claim("role", role.name)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
            .signWith(key)
            .compact()
    }

    fun isTokenValid(token: String, user: User): Boolean {
        val claims = getClaims(token)
        val tokenUsername = claims.subject
        val isTokenExpired = claims.expiration.before(Date())

        return tokenUsername == user.username && !isTokenExpired
    }

    fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    companion object {
        private const val ACCESS_TOKEN_EXPIRATION: Long = 7 * 24 * 60 * 60 * 1000 // 7 days
        private const val REFRESH_TOKEN_EXPIRATION: Long = 365 * 24 * 60 * 60 * 1000 // 365 days
    }
}