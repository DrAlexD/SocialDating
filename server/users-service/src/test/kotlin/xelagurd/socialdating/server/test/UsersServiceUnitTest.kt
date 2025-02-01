package xelagurd.socialdating.server.test

import org.springframework.data.repository.findByIdOrNull
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.model.User
import xelagurd.socialdating.server.model.enums.Gender
import xelagurd.socialdating.server.model.enums.Purpose
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.service.UsersService

class UsersServiceUnitTest {

    @MockK
    private lateinit var usersRepository: UsersRepository

    @InjectMockKs
    private lateinit var usersService: UsersService

    private val users = listOf(
        User(
            id = 1,
            name = "RemoteUser1",
            gender = Gender.MALE,
            username = "",
            password = "",
            email = "",
            age = 20,
            city = "",
            purpose = Purpose.RELATIONSHIPS,
            activity = 50
        ),
        User(
            id = 2,
            name = "RemoteUser2",
            gender = Gender.MALE,
            username = "",
            password = "",
            email = "",
            age = 20,
            city = "",
            purpose = Purpose.RELATIONSHIPS,
            activity = 50
        ),
        User(
            id = 3,
            name = "RemoteUser3",
            gender = Gender.MALE,
            username = "",
            password = "",
            email = "",
            age = 20,
            city = "",
            purpose = Purpose.RELATIONSHIPS,
            activity = 50
        )
    )

    private val userId = 1

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUsers() {
        val expected = users.filter { it.id == userId }

        assertEquals(expected.size, 1)

        every { usersRepository.findByIdOrNull(userId) } returns expected[0]

        val result = usersService.getUser(userId)

        assertEquals(expected[0], result)
    }
}