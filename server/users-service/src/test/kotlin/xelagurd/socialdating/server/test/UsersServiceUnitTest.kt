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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeUsersData
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.AGE_MIN
import xelagurd.socialdating.server.model.details.UsersFilterDetails
import xelagurd.socialdating.server.model.enums.Gender.FEMALE
import xelagurd.socialdating.server.model.enums.Purpose.ALL_AT_ONCE
import xelagurd.socialdating.server.model.enums.Purpose.FRIENDS
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
    private val userDtos = FakeUsersData.userDtos
    private val userDto = FakeUsersData.userDtos[0]

    @Test
    fun getUser_existData_returnsUser() {
        every { usersRepository.findById(any()) } returns Optional.of(user)

        val result = usersService.getUser(userId)

        assertEquals(userDto, result)

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

        assertEquals(userDtos, result)

        verify(exactly = 1) { usersRepository.findAllByIdIn(userIds) }
        confirmVerified(usersRepository)
    }

    @Test
    fun getUserIds_withoutFilter_findsUserIdsOfAnyAge() {
        every { usersRepository.findUserIds(any(), any(), any(), any(), any(), any()) } returns userIds

        val result = usersService.getUserIds(UsersFilterDetails())

        assertEquals(userIds, result)

        verify(exactly = 1) { usersRepository.findUserIds(null, AGE_MIN, AGE_MAX, null, null, null) }
        confirmVerified(usersRepository)
    }

    @Test
    fun getUserIds_withFilter_findsUserIdsWithAllAtOncePurposeAndCityInItsLanguages() {
        every { usersRepository.findById(any()) } returns Optional.of(user)
        every { usersRepository.findUserIds(any(), any(), any(), any(), any(), any()) } returns userIds

        val result = usersService.getUserIds(
            UsersFilterDetails(
                genders = listOf(FEMALE),
                ageMin = 20,
                ageMax = 30,
                purposes = listOf(FRIENDS),
                sameCityUserId = userId
            )
        )

        assertEquals(userIds, result)

        verify(exactly = 1) { usersRepository.findById(userId) }
        verify(exactly = 1) {
            usersRepository.findUserIds(
                listOf(FEMALE.name),
                20,
                30,
                listOf(FRIENDS.name, ALL_AT_ONCE.name),
                user.cityEn,
                user.cityRu
            )
        }
        confirmVerified(usersRepository)
    }

    @Test
    fun getUserIds_missingSameCityUser_returnsEmptyList() {
        every { usersRepository.findById(any()) } returns Optional.empty()

        val result = usersService.getUserIds(UsersFilterDetails(sameCityUserId = userId))

        assertTrue(result.isEmpty())

        verify(exactly = 1) { usersRepository.findById(userId) }
        verify(exactly = 0) { usersRepository.findUserIds(any(), any(), any(), any(), any(), any()) }
        confirmVerified(usersRepository)
    }
}
