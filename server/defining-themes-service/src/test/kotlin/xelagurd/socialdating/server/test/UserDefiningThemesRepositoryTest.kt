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
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.repository.UserDefiningThemesRepository

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserDefiningThemesRepositoryTest(
    @param:Autowired private val userDefiningThemesRepository: UserDefiningThemesRepository
) {

    private fun userDefiningTheme(userId: Int, definingThemeId: Int) =
        UserDefiningTheme(
            value = 50,
            interest = 25,
            userId = userId,
            definingThemeId = definingThemeId
        )

    @BeforeEach
    fun seedData() {
        userDefiningThemesRepository.saveAllAndFlush(
            listOf(
                userDefiningTheme(userId = 1, definingThemeId = 1),
                userDefiningTheme(userId = 1, definingThemeId = 2),
                userDefiningTheme(userId = 2, definingThemeId = 1)
            )
        )
    }

    @Test
    fun findAllByUserId_existData_returnsOnlyUsersThemes() {
        val result = userDefiningThemesRepository.findAllByUserId(1)

        assertEquals(2, result.size)
        assertTrue(result.all { it.userId == 1 })
        assertEquals(setOf(1, 2), result.map { it.definingThemeId }.toSet())
    }

    @Test
    fun findAllByUserId_noThemes_returnsEmptyList() {
        val result = userDefiningThemesRepository.findAllByUserId(999)

        assertTrue(result.isEmpty())
    }

    @Test
    fun findByUserIdAndDefiningThemeId_existing_returnsUserDefiningTheme() {
        val result = userDefiningThemesRepository.findByUserIdAndDefiningThemeId(1, 2)

        assertEquals(1, result?.userId)
        assertEquals(2, result?.definingThemeId)
    }

    @Test
    fun findByUserIdAndDefiningThemeId_notExisting_returnsNull() {
        assertNull(userDefiningThemesRepository.findByUserIdAndDefiningThemeId(1, 999))
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
