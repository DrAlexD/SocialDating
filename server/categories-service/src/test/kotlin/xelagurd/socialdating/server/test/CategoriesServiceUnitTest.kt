package xelagurd.socialdating.server.test

import kotlin.random.Random
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.repository.CategoriesRepository
import xelagurd.socialdating.server.service.CategoriesService
import xelagurd.socialdating.server.utils.TestUtils.nextIntList

@ExtendWith(MockKExtension::class)
class CategoriesServiceUnitTest {

    @MockK
    private lateinit var categoriesRepository: CategoriesRepository

    @InjectMockKs
    private lateinit var categoriesService: CategoriesService

    private val categoryIds = Random.nextIntList()
    private val categories = FakeCategoriesData.categories
    private val categoryDtos = FakeCategoriesData.categoryDtos
    private val categoryDetails = FakeCategoriesData.categoriesDetails[0]
    private val categorySlot = slot<Category>()

    @Test
    fun getCategories_withIds_returnsRepositoryResult() {
        every { categoriesRepository.findAllByIds(any()) } returns categories

        val result = categoriesService.getCategories(categoryIds)

        assertEquals(categoryDtos, result)

        verify(exactly = 1) { categoriesRepository.findAllByIds(categoryIds) }
        confirmVerified(categoriesRepository)
    }

    @Test
    fun getCategories_noIds_passesNullToRepository() {
        every { categoriesRepository.findAllByIds(any()) } returns categories

        val result = categoriesService.getCategories()

        assertEquals(categoryDtos, result)

        verify(exactly = 1) { categoriesRepository.findAllByIds(null) }
        confirmVerified(categoriesRepository)
    }

    @Test
    fun addCategory_validData_savesMappedCategoryWithNextOrderNumber() {
        val maxOrderNumber = 5
        every { categoriesRepository.findMaxOrderNumber() } returns maxOrderNumber
        every { categoriesRepository.save(capture(categorySlot)) } answers { categorySlot.captured.apply { id = 1 } }

        categoriesService.addCategory(categoryDetails)

        assertEquals(categoryDetails.nameEn, categorySlot.captured.nameEn)
        assertEquals(categoryDetails.nameRu, categorySlot.captured.nameRu)
        assertEquals(maxOrderNumber + 1, categorySlot.captured.orderNumber)

        verify(exactly = 1) { categoriesRepository.findMaxOrderNumber() }
        verify(exactly = 1) { categoriesRepository.save(any()) }
        confirmVerified(categoriesRepository)
    }

    @Test
    fun addCategory_emptyTable_savesMappedCategoryWithFirstOrderNumber() {
        every { categoriesRepository.findMaxOrderNumber() } returns null
        every { categoriesRepository.save(capture(categorySlot)) } answers { categorySlot.captured.apply { id = 1 } }

        categoriesService.addCategory(categoryDetails)

        assertEquals(ID_MIN, categorySlot.captured.orderNumber)

        verify(exactly = 1) { categoriesRepository.findMaxOrderNumber() }
        verify(exactly = 1) { categoriesRepository.save(any()) }
        confirmVerified(categoriesRepository)
    }
}
