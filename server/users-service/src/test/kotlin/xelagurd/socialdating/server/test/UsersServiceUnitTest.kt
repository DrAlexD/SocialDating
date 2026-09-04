package xelagurd.socialdating.server.test

import java.util.Optional
import kotlin.random.Random
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeUsersData
import xelagurd.socialdating.server.repository.UsersRepository
import xelagurd.socialdating.server.service.UsersService
import xelagurd.socialdating.server.utils.TestUtils.nextIntList

@ExtendWith(MockKExtension::class)
class UsersServiceUnitTest {

    @MockK
    private lateinit var usersRepository: UsersRepository

    @InjectMockKs
    private lateinit var usersService: UsersService

    private val userId = Random.nextInt(1, Int.MAX_VALUE)
    private val userIds = Random.nextIntList()
    private val users = FakeUsersData.users
    private val user = FakeUsersData.users[0]
    private val userResponses = FakeUsersData.userResponses
    private val userResponse = FakeUsersData.userResponses[0]

    @Test
    fun getUser_existData_returnsUser() {
        every { usersRepository.findById(any()) } returns Optional.of(user)

        val result = usersService.getUser(userId)

        assertEquals(userResponse, result)

        verify(exactly = 1) { usersRepository.findById(userId) }
        confirmVerified(usersRepository)
    }

    @Test
    fun getUser_noData_returnsNull() {
        every { usersRepository.findById(any()) } returns Optional.empty()

        val result = usersService.getUser(userId)

        assertNull(result)

        verify(exactly = 1) { usersRepository.findById(userId) }
        confirmVerified(usersRepository)
    }

    @Test
    fun getUsers_existData_returnsRepositoryResult() {
        every { usersRepository.findAllByIdIn(any()) } returns users

        val result = usersService.getUsers(userIds)

        assertEquals(userResponses, result)

        verify(exactly = 1) { usersRepository.findAllByIdIn(userIds) }
        confirmVerified(usersRepository)
    }
}
