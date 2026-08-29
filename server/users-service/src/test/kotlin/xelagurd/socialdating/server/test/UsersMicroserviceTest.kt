package xelagurd.socialdating.server.test

import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.PostgreSQLContainer
import xelagurd.socialdating.server.FakeUsersData
import xelagurd.socialdating.server.FakeUsersData.toUsersWithNullIds
import xelagurd.socialdating.server.model.DefaultDataProperties.USER_ACTIVITY_INITIAL
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.details.LoginDetails
import xelagurd.socialdating.server.model.details.RefreshTokenDetails
import xelagurd.socialdating.server.model.details.RegistrationDetails
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.utils.TestUtils.nextIntList
import xelagurd.socialdating.server.utils.TestUtils.readArrayFromJsonString
import xelagurd.socialdating.server.utils.TestUtils.readObject
import xelagurd.socialdating.server.utils.TestUtils.readObjectFromJsonString
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = [
        "JWT_SECRET=c29jaWFsZGF0aW5nLXRlc3Qtand0LXNlY3JldC1rZXktZm9yLW1pY3Jvc2VydmljZS10ZXN0cy0wMDAwMDAx",
        "ACCESS_TOKEN_EXPIRATION=900000",
        "REFRESH_TOKEN_EXPIRATION=604800000"
    ]
)
@Import(NoSecurityConfig::class, AuthTestConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersMicroserviceTest(
    @param:Autowired val restTemplate: TestRestTemplate,
    @param:Autowired val usersRepository: UsersRepository
) {

    private val users = FakeUsersData.users
    private val userIds = users.take(2).mapNotNull { it.id }

    private val existingUsersNumber = users.size + 1
    private val missingUserId = Random.nextInt(existingUsersNumber + 1, Int.MAX_VALUE)
    private val missingUserIds = Random.nextIntList(from = existingUsersNumber + 1)

    private val loginDetails = LoginDetails(username = users[0].username, password = "password1")
    private val registrationDetails = RegistrationDetails(
        name = "Nikolay",
        gender = Gender.MALE,
        username = "username4",
        password = "password4",
        email = "email4@gmail.com",
        age = 28,
        city = "Saint Petersburg",
        purpose = Purpose.FRIENDS
    )

    @BeforeAll
    fun addUsers() {
        usersRepository.saveAll(users.toUsersWithNullIds())
    }

    @Test
    fun getUser_existData_ok() {
        val response = restTemplate.getForEntity(
            "/users/${users[0].id}",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertUser(users[0], readObjectFromJsonString(response.body!!))
    }

    @Test
    fun getUser_noData_noContent() {
        val response = restTemplate.getForEntity(
            "/users/$missingUserId",
            String::class.java
        )
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun getUsers_existData_ok() {
        val expected = users.filter { it.id in userIds }
        val response = restTemplate.getForEntity(
            "/users?userIds=${userIds.toRequestParams()}",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseUsers = readArrayFromJsonString(response.body!!)
        assertEquals(expected.size, responseUsers.size)
        expected.forEachIndexed { index, user -> assertUser(user, responseUsers[index]) }
    }

    @Test
    fun getUsers_noData_noContent() {
        val response = restTemplate.getForEntity(
            "/users?userIds=${missingUserIds.toRequestParams()}",
            String::class.java
        )
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun loginUser_validData_ok() {
        val responseAuth = loginUser(loginDetails)

        assertUser(users[0], responseAuth.readObject("user"))
    }

    @Test
    fun registerUser_validData_ok() {
        val response = restTemplate.postForEntity(
            "/users/auth/register",
            registrationDetails,
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseAuth = readObjectFromJsonString(response.body!!)
        assertNotNull(responseAuth["accessToken"])
        assertNotNull(responseAuth["refreshToken"])

        val responseUser = responseAuth.readObject("user")
        assertNotNull(responseUser["id"])
        assertEquals(registrationDetails.name, responseUser["name"])
        assertEquals(registrationDetails.gender.name, responseUser["gender"])
        assertEquals(registrationDetails.username, responseUser["username"])
        assertEquals(registrationDetails.age, responseUser["age"])
        assertEquals(registrationDetails.city, responseUser["city"])
        assertEquals(registrationDetails.purpose.name, responseUser["purpose"])
        assertEquals(USER_ACTIVITY_INITIAL, responseUser["activity"])
        assertEquals(Role.USER.name, responseUser["role"])
    }

    @Test
    fun refreshToken_validData_ok() {
        val responseAuth = loginUser(loginDetails)
        val refreshTokenDetails = RefreshTokenDetails(refreshToken = responseAuth["refreshToken"] as String)

        val response = restTemplate.postForEntity(
            "/users/auth/refresh-token",
            refreshTokenDetails,
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseRefreshedAuth = readObjectFromJsonString(response.body!!)
        assertNotNull(responseRefreshedAuth["accessToken"])
        assertNotNull(responseRefreshedAuth["refreshToken"])
        assertUser(users[0], responseRefreshedAuth.readObject("user"))
    }

    private fun loginUser(loginDetails: LoginDetails): Map<String, Any> {
        val response = restTemplate.postForEntity(
            "/users/auth/login",
            loginDetails,
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseAuth = readObjectFromJsonString(response.body!!)
        assertNotNull(responseAuth["accessToken"])
        assertNotNull(responseAuth["refreshToken"])
        assertNotEquals(responseAuth["accessToken"], responseAuth["refreshToken"])

        return responseAuth
    }

    private fun assertUser(expected: User, responseUser: Map<String, Any>) {
        assertEquals(expected.id, responseUser["id"])
        assertEquals(expected.name, responseUser["name"])
        assertEquals(expected.gender.name, responseUser["gender"])
        assertEquals(expected.username, responseUser["username"])
        assertEquals(expected.age, responseUser["age"])
        assertEquals(expected.city, responseUser["city"])
        assertEquals(expected.purpose.name, responseUser["purpose"])
        assertEquals(expected.activity, responseUser["activity"])
        assertEquals(expected.role.name, responseUser["role"])
    }

    companion object {
        @ServiceConnection
        val postgresContainer = PostgreSQLContainer("postgres:18")
            .apply {
                withDatabaseName("test_db")
                withUsername("test_user")
                withPassword("test_password")
                withInitScript("init-schema.sql")
            }
    }
}
