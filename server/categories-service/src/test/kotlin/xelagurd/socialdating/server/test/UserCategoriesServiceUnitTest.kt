package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.client.UsersServiceClient
import xelagurd.socialdating.server.model.DefaultDataProperties.USER_ACTIVITY_INITIAL
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.UserCategoryData
import xelagurd.socialdating.server.model.dto.UserDto
import xelagurd.socialdating.server.model.enums.Gender.MALE
import xelagurd.socialdating.server.model.enums.Purpose.FRIENDS
import xelagurd.socialdating.server.model.enums.Role.USER
import xelagurd.socialdating.server.model.enums.SimilarityType.EQUAL
import xelagurd.socialdating.server.model.enums.SimilarityType.OPPOSITE
import xelagurd.socialdating.server.model.enums.SimilarityType.SIMILAR
import xelagurd.socialdating.server.repository.UserCategoriesRepository
import xelagurd.socialdating.server.service.UserCategoriesService

@ExtendWith(MockKExtension::class)
class UserCategoriesServiceUnitTest {

    @MockK
    private lateinit var userCategoriesRepository: UserCategoriesRepository

    @MockK
    private lateinit var usersServiceClient: UsersServiceClient

    @InjectMockKs
    private lateinit var userCategoriesService: UserCategoriesService

    private val currentUserId = Random.nextInt(1, Int.MAX_VALUE)
    private val anotherUserId = Random.nextInt(1, Int.MAX_VALUE)
    private val categoryId = Random.nextInt(1, Int.MAX_VALUE)

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    private fun setAuthenticatedUser(userId: Int) {
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns userId
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun userCategoryData(
        id: Int,
        name: String = "Category$id",
        maintained: Array<Long>? = null,
        notMaintained: Array<Long>? = null
    ) = UserCategoryData(id, name, name, maintained, notMaintained)

    private fun userDto(
        id: Int,
        name: String = "User$id",
        age: Int = 20 + id,
        city: String = "City$id"
    ) = UserDto(id, name, MALE, "username$id", age, city, FRIENDS, USER_ACTIVITY_INITIAL, USER)

    private fun userCategory(
        userId: Int,
        categoryId: Int,
        maintained: Array<Long>? = null,
        notMaintained: Array<Long>? = null
    ) = UserCategory(userId = userId, categoryId = categoryId, maintained = maintained, notMaintained = notMaintained)

    @Test
    fun getUserCategories_existData_returnsMappedDtos() {
        every { userCategoriesRepository.findAllByUserId(any()) } returns FakeCategoriesData.userCategories

        val result = userCategoriesService.getUserCategories(currentUserId)

        assertEquals(FakeCategoriesData.userCategoryDtos, result)

        verify(exactly = 1) { userCategoriesRepository.findAllByUserId(currentUserId) }
        confirmVerified(userCategoriesRepository)
    }

    @Test
    fun addUserCategory_validData_savesUserCategory() {
        val userCategory = FakeCategoriesData.userCategories[0]
        every { userCategoriesRepository.save(any()) } returns mockk()

        userCategoriesService.addUserCategory(userCategory)

        verify(exactly = 1) { userCategoriesRepository.save(userCategory) }
        confirmVerified(userCategoriesRepository)
    }

    @Test
    fun getUserCategory_existData_returnsRepositoryResult() {
        val userCategory = FakeCategoriesData.userCategories[0]
        every { userCategoriesRepository.findByUserIdAndCategoryId(any(), any()) } returns userCategory

        val result = userCategoriesService.getUserCategory(currentUserId, categoryId)

        assertEquals(userCategory, result)

        verify(exactly = 1) { userCategoriesRepository.findByUserIdAndCategoryId(currentUserId, categoryId) }
        confirmVerified(userCategoriesRepository)
    }

    @Test
    fun getSimilarUsers_anotherUser_throwsAccessDenied() {
        setAuthenticatedUser(currentUserId + 1)

        assertThrows<AccessDeniedException> {
            userCategoriesService.getSimilarUsers(currentUserId)
        }

        verify(exactly = 0) { userCategoriesRepository.findCurrentUserCategories(any(), any()) }
        verify(exactly = 0) { userCategoriesRepository.findAnotherUsersCategories(any(), any(), any()) }
        verify(exactly = 0) { usersServiceClient.getUsers(any()) }
        confirmVerified(userCategoriesRepository, usersServiceClient)
    }

    @Test
    fun getSimilarUsers_existData_returnsOnlySimilarUsersSortedByDifference() {
        setAuthenticatedUser(currentUserId)

        val currentUserCategories = listOf(
            userCategoryData(id = 1, maintained = arrayOf(0b0111L)),     // bits 1, 2, 3
            userCategoryData(id = 2, notMaintained = arrayOf(0b0011L)),  // bits 1, 2
            userCategoryData(id = 3, maintained = arrayOf(0b1000L))      // bit 4
        )
        every { userCategoriesRepository.findCurrentUserCategories(currentUserId, null) } returns currentUserCategories

        val anotherUsersCategories = listOf(
            // user 10: strongly similar (similar = 3, opposite = 0)
            userCategory(userId = 10, categoryId = 1, maintained = arrayOf(0b0011L)),    // 0111 & 0011 -> 2 similar
            userCategory(userId = 10, categoryId = 2, notMaintained = arrayOf(0b0001L)), // 0011 & 0001 -> 1 similar
            // user 20: weakly similar (similar = 1, opposite = 0)
            userCategory(userId = 20, categoryId = 1, maintained = arrayOf(0b0001L)),    // 0111 & 0001 -> 1 similar
            // user 30: opposite, excluded (similar = 0, opposite = 3)
            userCategory(userId = 30, categoryId = 1, notMaintained = arrayOf(0b0111L)), // maintained & notMaintained -> 3 opposite
            // user 40: no overlap, excluded (no similarity recorded)
            userCategory(userId = 40, categoryId = 3, maintained = arrayOf(0b0001L))     // 1000 & 0001 -> 0
        )
        every {
            userCategoriesRepository.findAnotherUsersCategories(currentUserId, null, listOf(1, 2, 3))
        } returns anotherUsersCategories

        every { usersServiceClient.getUsers(listOf(10, 20)) } returns listOf(userDto(20), userDto(10))

        val result = userCategoriesService.getSimilarUsers(currentUserId)

        assertEquals(listOf(10, 20), result.map { it.id })

        val similarUser10 = result[0]
        assertEquals(3, similarUser10.similarNumber)
        assertEquals(0, similarUser10.oppositeNumber)
        assertEquals("User10", similarUser10.name)
        assertEquals(30, similarUser10.age)
        assertEquals("City10", similarUser10.city)
        assertEquals(setOf("Category1", "Category2"), similarUser10.similarCategories.map { it.name }.toSet())
        assertTrue(similarUser10.oppositeCategories.isEmpty())

        verify(exactly = 1) { userCategoriesRepository.findCurrentUserCategories(currentUserId, null) }
        verify(exactly = 1) { userCategoriesRepository.findAnotherUsersCategories(currentUserId, null, listOf(1, 2, 3)) }
        verify(exactly = 1) { usersServiceClient.getUsers(listOf(10, 20)) }
        confirmVerified(userCategoriesRepository, usersServiceClient)
    }

    @Test
    fun getSimilarUsers_withoutUsersData_returnsOnlyUsersWithData() {
        setAuthenticatedUser(currentUserId)

        val currentUserCategories = listOf(userCategoryData(id = 1, maintained = arrayOf(0b0011L)))
        every { userCategoriesRepository.findCurrentUserCategories(currentUserId, null) } returns currentUserCategories

        val anotherUsersCategories = listOf(
            userCategory(userId = 10, categoryId = 1, maintained = arrayOf(0b0011L)),  // similar 2
            userCategory(userId = 20, categoryId = 1, maintained = arrayOf(0b0001L))   // similar 1
        )
        every {
            userCategoriesRepository.findAnotherUsersCategories(currentUserId, null, listOf(1))
        } returns anotherUsersCategories

        every { usersServiceClient.getUsers(listOf(10, 20)) } returns listOf(userDto(10))

        val result = userCategoriesService.getSimilarUsers(currentUserId)

        assertEquals(listOf(10), result.map { it.id })

        verify(exactly = 1) { userCategoriesRepository.findCurrentUserCategories(currentUserId, null) }
        verify(exactly = 1) { userCategoriesRepository.findAnotherUsersCategories(currentUserId, null, listOf(1)) }
        verify(exactly = 1) { usersServiceClient.getUsers(listOf(10, 20)) }
        confirmVerified(userCategoriesRepository, usersServiceClient)
    }

    @Test
    fun getSimilarUsers_manyCategories_appliesLimitsAndIncludesOppositeCategories() {
        setAuthenticatedUser(currentUserId)

        val currentUserCategories = listOf(
            userCategoryData(id = 1, maintained = arrayOf(0b0111L)),  // 3 bits
            userCategoryData(id = 2, maintained = arrayOf(0b0011L)),  // 2 bits
            userCategoryData(id = 3, maintained = arrayOf(0b0001L)),  // 1 bit
            userCategoryData(id = 4, maintained = arrayOf(0b0001L)),  // 1 bit
            userCategoryData(id = 5, maintained = arrayOf(0b0001L))   // 1 bit
        )
        every { userCategoriesRepository.findCurrentUserCategories(currentUserId, null) } returns currentUserCategories

        val anotherUsersCategories = listOf(
            userCategory(userId = 10, categoryId = 1, maintained = arrayOf(0b0111L)),     // similar 3 -> diff +3
            userCategory(userId = 10, categoryId = 2, maintained = arrayOf(0b0011L)),     // similar 2 -> diff +2
            userCategory(userId = 10, categoryId = 3, maintained = arrayOf(0b0001L)),     // similar 1 -> diff +1
            userCategory(userId = 10, categoryId = 4, notMaintained = arrayOf(0b0001L)),  // opposite 1 -> diff -1
            userCategory(userId = 10, categoryId = 5, notMaintained = arrayOf(0b0001L))   // opposite 1 -> diff -1
        )
        every {
            userCategoriesRepository.findAnotherUsersCategories(currentUserId, null, listOf(1, 2, 3, 4, 5))
        } returns anotherUsersCategories

        every { usersServiceClient.getUsers(listOf(10)) } returns listOf(userDto(10))

        val result = userCategoriesService.getSimilarUsers(currentUserId)

        assertEquals(1, result.size)
        val similarUser = result.single()
        assertEquals(6, similarUser.similarNumber)
        assertEquals(2, similarUser.oppositeNumber)
        // SIMILAR_CATEGORIES_NUMBER = 2, only the top 2 of 3 similar categories are kept, sorted descending
        assertEquals(listOf(3, 2), similarUser.similarCategories.map { it.differenceNumber })
        // OPPOSITE_CATEGORIES_NUMBER = 2
        assertEquals(2, similarUser.oppositeCategories.size)
        assertTrue(similarUser.oppositeCategories.all { it.differenceNumber == -1 })

        verify(exactly = 1) { userCategoriesRepository.findCurrentUserCategories(currentUserId, null) }
        verify(exactly = 1) {
            userCategoriesRepository.findAnotherUsersCategories(currentUserId, null, listOf(1, 2, 3, 4, 5))
        }
        verify(exactly = 1) { usersServiceClient.getUsers(listOf(10)) }
        confirmVerified(userCategoriesRepository, usersServiceClient)
    }

    @Test
    fun getSimilarUsers_noCurrentUserCategories_passesCategoryIdsAndReturnsEmpty() {
        setAuthenticatedUser(currentUserId)
        val categoryIds = listOf(1, 2)

        every { userCategoriesRepository.findCurrentUserCategories(currentUserId, categoryIds) } returns emptyList()
        every {
            userCategoriesRepository.findAnotherUsersCategories(currentUserId, null, emptyList())
        } returns emptyList()

        val result = userCategoriesService.getSimilarUsers(currentUserId, categoryIds)

        assertTrue(result.isEmpty())

        verify(exactly = 1) { userCategoriesRepository.findCurrentUserCategories(currentUserId, categoryIds) }
        verify(exactly = 1) { userCategoriesRepository.findAnotherUsersCategories(currentUserId, null, emptyList()) }
        verify(exactly = 0) { usersServiceClient.getUsers(any()) }
        confirmVerified(userCategoriesRepository, usersServiceClient)
    }

    @Test
    fun getDetailedSimilarUser_anotherUser_throwsAccessDenied() {
        setAuthenticatedUser(currentUserId + 1)

        assertThrows<AccessDeniedException> {
            userCategoriesService.getDetailedSimilarUser(currentUserId, anotherUserId)
        }

        verify(exactly = 0) { userCategoriesRepository.findCurrentUserCategories(any(), any()) }
        confirmVerified(userCategoriesRepository)
    }

    @Test
    fun getDetailedSimilarUser_existData_returnsSimilarityWithDefiningThemes() {
        setAuthenticatedUser(currentUserId)

        val currentUserCategories = listOf(
            // index 0: bits 1, 2, 4; index 1: bit 1 -> defining theme 65
            userCategoryData(id = 1, maintained = arrayOf(0b1011L, 0b0001L)),
            userCategoryData(id = 2, maintained = arrayOf(0b0001L), notMaintained = arrayOf(0b0010L)),
            userCategoryData(id = 3, notMaintained = arrayOf(0b0100L)),
            // bit 4, no overlap with another user -> neither similar nor opposite (category skipped)
            userCategoryData(id = 4, maintained = arrayOf(0b1000L))
        )
        every { userCategoriesRepository.findCurrentUserCategories(currentUserId, null) } returns currentUserCategories

        val anotherUserCategories = listOf(
            // similar themes 1, 2 (index 0) and 65 (index 1)
            userCategory(userId = 10, categoryId = 1, maintained = arrayOf(0b0011L, 0b0001L)),
            // opposite themes 1 and 2
            userCategory(userId = 10, categoryId = 2, maintained = arrayOf(0b0010L), notMaintained = arrayOf(0b0001L)),
            // theme 3 is both similar and opposite -> category similarity is EQUAL
            userCategory(userId = 10, categoryId = 3, maintained = arrayOf(0b0100L), notMaintained = arrayOf(0b0100L)),
            // bit 1 vs current bit 4 -> no overlap -> category produces no similarity at all
            userCategory(userId = 10, categoryId = 4, maintained = arrayOf(0b0001L))
        )
        every {
            userCategoriesRepository.findAnotherUsersCategories(currentUserId, anotherUserId, listOf(1, 2, 3, 4))
        } returns anotherUserCategories

        val result = userCategoriesService.getDetailedSimilarUser(currentUserId, anotherUserId)

        assertEquals(4, result.similarNumber)
        assertEquals(3, result.oppositeNumber)
        assertEquals(3, result.categories.size)
        assertFalse(result.categories.containsKey(4))

        val category1 = result.categories.getValue(1)
        assertEquals(SIMILAR, category1.similarityType)
        assertEquals(3, category1.similarNumber)
        assertEquals(0, category1.oppositeNumber)
        assertEquals(3, category1.differenceNumber)
        assertEquals(setOf(1, 2, 65), category1.definingThemes.keys)
        assertTrue(category1.definingThemes.values.all { it.similarityType == SIMILAR })

        val category2 = result.categories.getValue(2)
        assertEquals(OPPOSITE, category2.similarityType)
        assertEquals(0, category2.similarNumber)
        assertEquals(2, category2.oppositeNumber)
        assertEquals(-2, category2.differenceNumber)
        assertEquals(setOf(1, 2), category2.definingThemes.keys)
        assertTrue(category2.definingThemes.values.all { it.similarityType == OPPOSITE })

        val category3 = result.categories.getValue(3)
        assertEquals(EQUAL, category3.similarityType)
        assertEquals(1, category3.similarNumber)
        assertEquals(1, category3.oppositeNumber)
        assertEquals(0, category3.differenceNumber)

        verify(exactly = 1) { userCategoriesRepository.findCurrentUserCategories(currentUserId, null) }
        verify(exactly = 1) {
            userCategoriesRepository.findAnotherUsersCategories(currentUserId, anotherUserId, listOf(1, 2, 3, 4))
        }
        confirmVerified(userCategoriesRepository)
    }
}
