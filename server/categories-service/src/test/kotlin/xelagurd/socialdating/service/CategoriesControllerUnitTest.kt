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

class CategoriesControllerUnitTest {

    @MockK
    private lateinit var categoriesRepository: CategoriesRepository

    @InjectMockKs
    private lateinit var categoriesService: CategoriesService

    private val category = Category(id = 1, name = "RemoteCategory1")
    private val categoryDetails = CategoryDetails(name = "RemoteCategory1")

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getCategories() {
        every { categoriesRepository.findAll() } returns listOf(category)

        val result = categoriesService.getCategories()

        assertEquals(
            listOf(category),
            result
        )
    }

    @Test
    fun addCategory() {
        every { categoriesRepository.save(categoryDetails.toCategory()) } returns category

        val result = categoriesService.addCategory(categoryDetails)

        assertEquals(
            category,
            result
        )
    }
}