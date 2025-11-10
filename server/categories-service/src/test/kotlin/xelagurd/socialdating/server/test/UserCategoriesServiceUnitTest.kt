package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.repository.UserCategoriesRepository
import xelagurd.socialdating.server.service.UserCategoriesService

class UserCategoriesServiceUnitTest {

    @MockK
    private lateinit var userCategoriesRepository: UserCategoriesRepository

    @InjectMockKs
    private lateinit var userCategoriesService: UserCategoriesService

    private val userId = 1
    private val categoryId = 1

    private val userCategories = FakeCategoriesData.userCategories
    private val userCategory = userCategories[0]

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUserCategory() {
        val expected = userCategory
        every { userCategoriesRepository.findByUserIdAndCategoryId(userId, categoryId) } returns expected

        val result = userCategoriesService.getUserCategory(userId, categoryId)

        assertEquals(expected, result)
    }

    @Test
    fun getUserCategories_allData_success() {
        val expected = userCategories
        every { userCategoriesRepository.findAllByUserId(userId) } returns expected

        val result = userCategoriesService.getUserCategories(userId)

        assertEquals(expected, result)
    }

    @Test
    fun getUserCategories_emptyData_error() {
        every { userCategoriesRepository.findAllByUserId(userId) } returns emptyList()

        assertThrows<NoDataFoundException> { userCategoriesService.getUserCategories(userId) }
    }

}