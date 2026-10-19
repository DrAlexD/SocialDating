package xelagurd.socialdating.server.test

import java.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.postgresql.PostgreSQLContainer
import xelagurd.socialdating.server.FakeUsersData
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Gender.FEMALE
import xelagurd.socialdating.server.model.enums.Gender.MALE
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.model.enums.Purpose.ALL_AT_ONCE
import xelagurd.socialdating.server.model.enums.Purpose.FRIENDS
import xelagurd.socialdating.server.model.enums.Purpose.RELATIONSHIPS
import xelagurd.socialdating.server.model.enums.Role
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.utils.DateUtils.toEarliestBirthDate
import xelagurd.socialdating.server.utils.DateUtils.toLatestBirthDate

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersRepositoryTest(
    @param:Autowired private val usersRepository: UsersRepository
) {

    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User

    private fun user(
        name: String,
        username: String,
        telegram: String = "@$username",
        gender: Gender = MALE,
        birthDate: LocalDate = LocalDate.of(2000, 1, 1),
        cityEn: String? = "city",
        cityRu: String? = null,
        purpose: Purpose = FRIENDS
    ) =
        User(
            nameEn = name,
            gender = gender,
            username = username,
            password = FakeUsersData.users[0].password,
            telegram = telegram,
            birthDate = birthDate,
            cityEn = cityEn,
            cityRu = cityRu,
            purpose = purpose,
            role = Role.USER
        )

    @BeforeEach
    fun seedData() {
        val saved = usersRepository.saveAllAndFlush(
            listOf(
                user(name = "Alexander", username = "username1", telegram = "@telegram1"),
                user(name = "Denis", username = "username2", telegram = "@Telegram2"),
                user(name = "Andrey", username = "username3", telegram = "@telegram3")
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
    fun findByTelegram_existing_returnsUser() {
        val result = usersRepository.findByTelegram("@Telegram2")

        assertEquals(user2.id, result?.id)
        assertEquals("@Telegram2", result?.telegram)
    }

    @Test
    fun findByTelegram_existingInOtherCase_returnsNull() {
        assertNull(usersRepository.findByTelegram("@telegram2"))
    }

    @Test
    fun findByTelegram_notExisting_returnsNull() {
        assertNull(usersRepository.findByTelegram("@missing"))
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

    @Test
    fun findUserIds_withoutFilter_returnsAllUserIds() {
        val result = usersRepository.findUserIds(null, null, null, null, null, null)

        assertEquals(setOf(user1.id, user2.id, user3.id), result.toSet())
    }

    @Test
    fun findUserIds_byBirthDates_includesBorders() {
        val (earliestUser, latestUser) = usersRepository.saveAllAndFlush(
            listOf(
                user(name = "Maria", username = "username4", birthDate = LocalDate.of(1990, 5, 10)),
                user(name = "Anna", username = "username5", birthDate = LocalDate.of(1995, 5, 10)),
                user(name = "Olga", username = "username6", birthDate = LocalDate.of(1990, 5, 9)),
                user(name = "Irina", username = "username7", birthDate = LocalDate.of(1995, 5, 11))
            )
        )

        val result = usersRepository.findUserIds(
            null,
            earliestUser.birthDate,
            latestUser.birthDate,
            null,
            null,
            null
        )

        assertEquals(setOf(earliestUser.id, latestUser.id), result.toSet())
    }

    @Test
    fun findUserIds_byGendersAgeAndPurposes_returnsOnlyMatching() {
        val today = LocalDate.now()
        val suitableBirthDate = today.minusYears(30)

        // only the first user has the suitable age and purpose
        val matchingUser = usersRepository.saveAllAndFlush(
            listOf(
                user(
                    name = "Maria",
                    username = "username4",
                    gender = FEMALE,
                    birthDate = suitableBirthDate,
                    purpose = ALL_AT_ONCE
                ),
                user(
                    name = "Anna",
                    username = "username5",
                    gender = FEMALE,
                    birthDate = today.minusYears(40),
                    purpose = ALL_AT_ONCE
                ),
                user(
                    name = "Olga",
                    username = "username6",
                    gender = FEMALE,
                    birthDate = suitableBirthDate,
                    purpose = FRIENDS
                )
            )
        ).first()

        val result = usersRepository.findUserIds(
            listOf(FEMALE.name),
            35.toEarliestBirthDate(today),
            30.toLatestBirthDate(today),
            listOf(RELATIONSHIPS.name, ALL_AT_ONCE.name),
            null,
            null
        )

        assertEquals(listOf(matchingUser.id), result)
    }

    @Test
    fun findUserIds_byCity_comparesCityOnlyInItsLanguagesIgnoringCaseAndSpaces() {
        val (bothLanguagesUser, russianUser, englishUser) = usersRepository.saveAllAndFlush(
            listOf(
                user(name = "Ivan", username = "username4", cityEn = "Moscow", cityRu = "Москва"),
                user(name = "Petr", username = "username5", cityEn = null, cityRu = "Москва"),
                user(name = "John", username = "username6", cityEn = "Moscow", cityRu = null)
            )
        )

        // the translation of the city is unknown, so the users without the city in the same language are not found
        val russianResult = usersRepository.findUserIds(null, null, null, null, null, " москва")
        assertEquals(setOf(bothLanguagesUser.id, russianUser.id), russianResult.toSet())

        val englishResult = usersRepository.findUserIds(null, null, null, null, "MOSCOW ", null)
        assertEquals(setOf(bothLanguagesUser.id, englishUser.id), englishResult.toSet())

        // the city in both languages is the same for the users with the city in any of them
        val bothLanguagesResult = usersRepository.findUserIds(null, null, null, null, "Moscow", "Москва")
        assertEquals(setOf(bothLanguagesUser.id, russianUser.id, englishUser.id), bothLanguagesResult.toSet())
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
