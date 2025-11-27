package xelagurd.socialdating.server.security

import java.util.Base64
import java.util.Date
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import xelagurd.socialdating.server.model.enums.Role

@Component
class JwtGenerationUtils(
    private val env: Environment
) {

    private val key by lazy {
        val secret = env.getProperty("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET is not set")
        Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))
    }

    private val accessTokenExpiration by lazy {
        env.getProperty("ACCESS_TOKEN_EXPIRATION")?.toLong()
            ?: throw IllegalStateException("ACCESS_TOKEN_EXPIRATION is not set")
    }

    private val refreshTokenExpiration by lazy {
        env.getProperty("REFRESH_TOKEN_EXPIRATION")?.toLong()
            ?: throw IllegalStateException("REFRESH_TOKEN_EXPIRATION is not set")
    }

    fun generateAccessToken(username: String, role: Role): String =
        Jwts.builder()
            .subject(username)
            .claim("role", role.name)
            .claim("type", "access")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(key)
            .compact()

    fun generateRefreshToken(username: String): String =
        Jwts.builder()
            .subject(username)
            .claim("type", "refresh")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(key)
            .compact()

    fun isRefreshToken(refreshToken: String): Boolean {
        val claims = getClaims(refreshToken)

        return claims["type"] == "refresh" && claims.subject != null
    }

    fun isRefreshTokenValid(refreshToken: String): Boolean {
        val claims = getClaims(refreshToken)
        val isTokenExpired = claims.expiration.before(Date())

        return !isTokenExpired
    }

    fun getClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}