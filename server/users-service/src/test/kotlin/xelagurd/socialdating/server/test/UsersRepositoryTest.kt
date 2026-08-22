package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Role
import xelagurd.socialdating.server.repository.UsersRepository

@ActiveProfiles("dev", "test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersRepositoryTest(
    @param:Autowired private val usersRepository: UsersRepository
) {

    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User

    private fun user(name: String, username: String, email: String?) =
        User(
            name = name,
            gender = Gender.MALE,
            username = username,
            password = "password",
            email = email,
            age = 25,
            city = "city",
            purpose = Purpose.FRIENDS,
            role = Role.USER
        )

    @BeforeEach
    fun seedData() {
        val saved = usersRepository.saveAllAndFlush(
            listOf(
                user(name = "Alexander", username = "username1", email = "email1@gmail.com"),
                user(name = "Denis", username = "username2", email = "email2@gmail.com"),
                user(name = "Andrey", username = "username3", email = null)
            )
        )
        user1 = saved[0]
        user2 = saved[1]
        user3 = saved[2]
    }

    @Test
    fun findByUsername_existing_returnsUser() {
        val result = usersRepository.findByUsername("username1")

        assertEquals(user1.id, result?.id)
        assertEquals("username1", result?.username)
    }

    @Test
    fun findByUsername_notExisting_returnsNull() {
        assertNull(usersRepository.findByUsername("unknown"))
    }

    @Test
    fun findByEmail_existing_returnsUser() {
        val result = usersRepository.findByEmail("email2@gmail.com")

        assertEquals(user2.id, result?.id)
        assertEquals("email2@gmail.com", result?.email)
    }

    @Test
    fun findByEmail_notExisting_returnsNull() {
        assertNull(usersRepository.findByEmail("missing@gmail.com"))
    }

    @Test
    fun findAllByIdIn_existData_returnsMatchingUsers() {
        val result = usersRepository.findAllByIdIn(listOf(user1.id!!, user3.id!!))

        assertEquals(2, result.size)
        assertEquals(setOf(user1.id, user3.id), result.map { it.id }.toSet())
    }

    @Test
    fun findAllByIdIn_noMatches_returnsEmptyList() {
        assertTrue(usersRepository.findAllByIdIn(listOf(999_999)).isEmpty())
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
