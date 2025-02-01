package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.repository.UserCategoriesRepository
import xelagurd.socialdating.server.service.UserCategoriesService

class UserCategoriesServiceUnitTest {

    @MockK
    private lateinit var userCategoriesRepository: UserCategoriesRepository

    @InjectMockKs
    private lateinit var userCategoriesService: UserCategoriesService

    private val userId = 1
    private val categoryId = 1

    private val userCategories = listOf(
        UserCategory(id = 1, interest = 10, userId = 1, categoryId = 1),
        UserCategory(id = 2, interest = 15, userId = 1, categoryId = 2),
        UserCategory(id = 3, interest = 20, userId = 2, categoryId = 3)
    )

    private val newUserCategory = UserCategory(interest = 10, userId = 1, categoryId = 1)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUserCategory() {
        val expected = userCategories.filter { it.userId == userId && it.categoryId == categoryId }

        assertEquals(expected.size, 1)

        every { userCategoriesRepository.findByUserIdAndCategoryId(userId, categoryId) } returns expected[0]

        val result = userCategoriesService.getUserCategory(userId, categoryId)

        assertEquals(expected[0], result)
    }

    @Test
    fun getUserCategories() {
        val expected = userCategories.filter { it.userId == userId }
        every { userCategoriesRepository.findAllByUserId(userId) } returns expected

        val result = userCategoriesService.getUserCategories(userId)

        assertEquals(expected, result)
    }

    @Test
    fun addUserCategory() {
        val expected = userCategories[0]
        every { userCategoriesRepository.save(newUserCategory) } returns expected

        val result = userCategoriesService.addUserCategory(newUserCategory)

        assertEquals(expected, result)
    }
}