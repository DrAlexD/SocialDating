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
import xelagurd.socialdating.server.repository.CategoriesRepository
import xelagurd.socialdating.server.service.CategoriesService

class CategoriesServiceUnitTest {

    @MockK
    private lateinit var categoriesRepository: CategoriesRepository

    @InjectMockKs
    private lateinit var categoriesService: CategoriesService

    private val categories = FakeCategoriesData.categories

    private val categoryDetails = FakeCategoriesData.categoriesDetails[0]
    private val category = categories[0]

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getCategories_allData_success() {
        val expected = categories
        every { categoriesRepository.findAll() } returns expected

        val result = categoriesService.getCategories()

        assertEquals(expected, result)
    }

    @Test
    fun getCategories_emptyData_error() {
        every { categoriesRepository.findAll() } returns emptyList()

        assertThrows<NoDataFoundException> { categoriesService.getCategories() }
    }

    @Test
    fun addCategory() {
        val expected = category
        every { categoriesRepository.save(categoryDetails.toCategory()) } returns expected

        val result = categoriesService.addCategory(categoryDetails)

        assertEquals(expected, result)
    }
}