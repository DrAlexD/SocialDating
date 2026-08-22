package xelagurd.socialdating.server.test

import java.util.Base64
import java.util.Date
import kotlin.random.Random
import org.springframework.core.env.Environment
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.model.enums.Role
import xelagurd.socialdating.server.security.JwtGenerationUtils

class JwtGenerationUtilsUnitTest {

    private val secret = Base64.getEncoder().encodeToString(ByteArray(64) { (it + 1).toByte() })
    private val accessTokenExpiration = 3_600_000L
    private val refreshTokenExpiration = 86_400_000L

    private val key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))

    private val username = "username"
    private val role = Role.ADMIN
    private val userId = Random.nextInt(1, Int.MAX_VALUE)

    private lateinit var jwtUtils: JwtGenerationUtils

    private fun environment(
        secret: String? = this.secret,
        accessTokenExpiration: String? = this.accessTokenExpiration.toString(),
        refreshTokenExpiration: String? = this.refreshTokenExpiration.toString()
    ): Environment {
        val environment = mockk<Environment>()
        every { environment.getProperty("JWT_SECRET") } returns secret
        every { environment.getProperty("ACCESS_TOKEN_EXPIRATION") } returns accessTokenExpiration
        every { environment.getProperty("REFRESH_TOKEN_EXPIRATION") } returns refreshTokenExpiration
        return environment
    }

    @BeforeEach
    fun setup() {
        jwtUtils = JwtGenerationUtils(environment())
    }

    @Test
    fun isRefreshToken_refreshToken_true() {
        val token = jwtUtils.generateRefreshToken(username)

        assertTrue(jwtUtils.isRefreshToken(token))
    }

    @Test
    fun isRefreshToken_accessToken_false() {
        val token = jwtUtils.generateAccessToken(username, role, userId)

        assertFalse(jwtUtils.isRefreshToken(token))
    }

    @Test
    fun isRefreshToken_refreshTokenWithoutSubject_false() {
        val token = Jwts.builder()
            .claim("type", "refresh")
            .expiration(Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(key)
            .compact()

        assertFalse(jwtUtils.isRefreshToken(token))
    }

    @Test
    fun isRefreshTokenValid_notExpired_true() {
        val token = jwtUtils.generateRefreshToken(username)

        assertTrue(jwtUtils.isRefreshTokenValid(token))
    }

    @Test
    fun isRefreshTokenValid_expired_false() {
        val spiedJwtUtils = spyk(jwtUtils)
        val claims = mockk<Claims> {
            every { expiration } returns Date(System.currentTimeMillis() - refreshTokenExpiration)
        }
        every { spiedJwtUtils.getClaims("token") } returns claims

        assertFalse(spiedJwtUtils.isRefreshTokenValid("token"))
    }
}
