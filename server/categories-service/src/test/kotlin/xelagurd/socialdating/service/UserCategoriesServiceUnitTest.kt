package xelagurd.socialdating.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.dto.UserCategory
import xelagurd.socialdating.dto.UserCategoryDetails
import xelagurd.socialdating.repository.UserCategoriesRepository

class UserCategoriesServiceUnitTest {

    @MockK
    private lateinit var userCategoriesRepository: UserCategoriesRepository

    @InjectMockKs
    private lateinit var userCategoriesService: UserCategoriesService

    private val userId = 1

    private val userCategories = listOf(
        UserCategory(id = 1, interest = 10, userId = 1, categoryId = 1),
        UserCategory(id = 2, interest = 15, userId = 1, categoryId = 2),
        UserCategory(id = 3, interest = 20, userId = 2, categoryId = 3)
    )

    private val userCategoryDetails = UserCategoryDetails(interest = 10, userId = 1, categoryId = 1)

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUserCategories() {
        val expected = userCategories.filter { it.id == userId }
        every { userCategoriesRepository.findAllByUserId(userId) } returns expected

        val result = userCategoriesService.getUserCategories(userId)

        assertEquals(expected, result)
    }

    @Test
    fun addUserCategory() {
        val expected = userCategories[0]
        every { userCategoriesRepository.save(userCategoryDetails.toUserCategory()) } returns expected

        val result = userCategoriesService.addUserCategory(userCategoryDetails)

        assertEquals(expected, result)
    }
}