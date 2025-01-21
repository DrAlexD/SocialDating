package xelagurd.socialdating.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.dto.Category
import xelagurd.socialdating.dto.CategoryDetails
import xelagurd.socialdating.repository.CategoriesRepository

class CategoriesServiceUnitTest {

    @MockK
    private lateinit var categoriesRepository: CategoriesRepository

    @InjectMockKs
    private lateinit var categoriesService: CategoriesService

    private val categories = listOf(
        Category(id = 1, name = "RemoteCategory1"),
        Category(id = 2, name = "RemoteCategory2"),
        Category(id = 3, name = "RemoteCategory3")
    )
    private val categoryDetails = CategoryDetails(name = "RemoteCategory1")

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getCategories() {
        val expected = categories
        every { categoriesRepository.findAll() } returns expected

        val result = categoriesService.getCategories()

        assertEquals(expected, result)
    }

    @Test
    fun addCategory() {
        val expected = categories[0]
        every { categoriesRepository.save(categoryDetails.toCategory()) } returns expected

        val result = categoriesService.addCategory(categoryDetails)

        assertEquals(expected, result)
    }
}