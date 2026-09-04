package xelagurd.socialdating.server.service

import kotlin.math.max
import kotlin.math.min
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xelagurd.socialdating.server.exception.InvalidDataException
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.model.details.CategoryOrderDetails
import xelagurd.socialdating.server.model.dto.CategoryDto
import xelagurd.socialdating.server.model.enums.AppLanguage
import xelagurd.socialdating.server.repository.CategoriesRepository

@Service
class CategoriesService(
    private val categoriesRepository: CategoriesRepository
) {

    fun getCategories(categoryIds: List<Int>? = null): List<CategoryDto> {
        val language = AppLanguage.current()

        return categoriesRepository.findAllByIds(categoryIds).map { it.toCategoryDto(language) }
    }

    fun addCategory(categoryDetails: CategoryDetails): CategoryDto {
        val orderNumber = categoriesRepository.findMaxOrderNumber()?.plus(1)

        return categoriesRepository.save(categoryDetails.toCategory(orderNumber)).toCategoryDto()
    }

    @Transactional
    fun moveCategory(categoryOrderDetails: CategoryOrderDetails): CategoryDto {
        val categoryId = categoryOrderDetails.categoryId
        val targetOrderNumber = categoryOrderDetails.orderNumber

        val category = categoriesRepository.findByIdOrNull(categoryId)
            ?: throw InvalidDataException("error.category.notFound", categoryId)

        val maxOrderNumber = categoriesRepository.findMaxOrderNumber() ?: category.orderNumber

        if (targetOrderNumber > maxOrderNumber) {
            throw InvalidDataException("error.orderNumber.outOfRange", ID_MIN, maxOrderNumber)
        }

        val currentOrderNumber = category.orderNumber
        if (targetOrderNumber == currentOrderNumber) return category.toCategoryDto()

        val lowOrderNumber = min(currentOrderNumber, targetOrderNumber)
        val highOrderNumber = max(currentOrderNumber, targetOrderNumber)
        val shift = maxOrderNumber * SHIFT_MULTIPLIER

        categoriesRepository.shiftOrderNumbersOutOfRange(
            lowOrderNumber = lowOrderNumber,
            highOrderNumber = highOrderNumber,
            shift = shift
        )
        categoriesRepository.applyShiftedOrderNumbers(
            categoryId = categoryId,
            targetOrderNumber = targetOrderNumber,
            lowOrderNumber = lowOrderNumber,
            highOrderNumber = highOrderNumber,
            shift = shift,
            step = if (targetOrderNumber < currentOrderNumber) 1 else -1
        )

        return categoriesRepository.findByIdOrNull(categoryId)!!.toCategoryDto()
    }

    private companion object {
        const val SHIFT_MULTIPLIER = 2
    }
}