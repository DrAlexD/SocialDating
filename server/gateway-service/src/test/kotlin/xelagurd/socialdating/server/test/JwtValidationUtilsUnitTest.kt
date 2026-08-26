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
import xelagurd.socialdating.server.security.JwtValidationUtils

class JwtValidationUtilsUnitTest {

    private val secret = Base64.getEncoder().encodeToString(ByteArray(64) { (it + 1).toByte() })
    private val tokenExpiration = 3_600_000L

    private val key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret))

    private val username = "username"
    private val role = "ADMIN"
    private val userId = Random.nextInt(1, Int.MAX_VALUE)

    private lateinit var jwtUtils: JwtValidationUtils

    private fun environment(secret: String? = this.secret): Environment {
        val environment = mockk<Environment>()
        every { environment.getProperty("JWT_SECRET") } returns secret
        return environment
    }

    @BeforeEach
    fun setup() {
        jwtUtils = JwtValidationUtils(environment())
    }

    private fun buildToken(
        type: String? = "access",
        subject: String? = username,
        role: String? = this.role,
        userId: Int? = this.userId,
        expiration: Date = Date(System.currentTimeMillis() + tokenExpiration)
    ): String {
        val builder = Jwts.builder()
        subject?.let { builder.subject(it) }
        type?.let { builder.claim("type", it) }
        role?.let { builder.claim("role", it) }
        userId?.let { builder.claim("userId", it) }
        return builder.expiration(expiration).signWith(key).compact()
    }

    @Test
    fun isAccessToken_accessToken_true() {
        assertTrue(jwtUtils.isAccessToken(buildToken()))
    }

    @Test
    fun isAccessToken_nonAccessToken_false() {
        assertFalse(jwtUtils.isAccessToken(buildToken(type = "refresh")))
    }

    @Test
    fun isAccessToken_accessTokenWithoutSubject_false() {
        assertFalse(jwtUtils.isAccessToken(buildToken(subject = null)))
    }

    @Test
    fun isAccessToken_accessTokenWithoutRole_false() {
        assertFalse(jwtUtils.isAccessToken(buildToken(role = null)))
    }

    @Test
    fun isAccessTokenValid_notExpired_true() {
        assertTrue(jwtUtils.isAccessTokenValid(buildToken()))
    }

    @Test
    fun isAccessTokenValid_expired_false() {
        val spiedJwtUtils = spyk(jwtUtils)
        val claims = mockk<Claims> {
            every { expiration } returns Date(System.currentTimeMillis() - tokenExpiration)
        }
        every { spiedJwtUtils.getClaims("token") } returns claims

        assertFalse(spiedJwtUtils.isAccessTokenValid("token"))
    }
}
