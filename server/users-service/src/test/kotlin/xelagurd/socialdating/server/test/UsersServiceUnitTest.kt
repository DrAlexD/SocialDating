package xelagurd.socialdating.server.test

import org.springframework.data.repository.findByIdOrNull
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xelagurd.socialdating.server.FakeUsersData
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.service.UsersService

class UsersServiceUnitTest {

    @MockK
    private lateinit var usersRepository: UsersRepository

    @InjectMockKs
    private lateinit var usersService: UsersService

    private val userId = 1

    private val users = FakeUsersData.users
    private val user = users[0]

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUser_allData_success() {
        val expected = user
        every { usersRepository.findByIdOrNull(userId) } returns expected

        val result = usersService.getUser(userId)

        assertEquals(expected, result)
    }

    @Test
    fun getUser_emptyData_error() {
        every { usersRepository.findByIdOrNull(userId) } returns null

        assertThrows<NoDataFoundException> { usersService.getUser(userId) }
    }
}