package xelagurd.socialdating.server.service

import kotlin.math.max
import kotlin.math.min
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xelagurd.socialdating.server.model.DefaultDataProperties.ID_MIN
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.model.details.DefiningThemeOrderDetails
import xelagurd.socialdating.server.repository.DefiningThemesRepository

@Service
class DefiningThemesService(
    private val definingThemesRepository: DefiningThemesRepository
) {

    fun getDefiningThemes(
        definingThemeIds: List<Int>? = null,
        categoryId: Int? = null
    ) =
        definingThemesRepository.findAllByIdsAndCategoryId(definingThemeIds, categoryId)

    fun getDefiningTheme(definingThemeId: Int) =
        definingThemesRepository.findByIdOrNull(definingThemeId)

    fun addDefiningTheme(definingThemeDetails: DefiningThemeDetails): DefiningTheme {
        val numberInCategory = definingThemesRepository.findMaxNumberInCategory(definingThemeDetails.categoryId)
            ?.plus(1)
        return definingThemesRepository.save(definingThemeDetails.toDefiningTheme(numberInCategory))
    }

    @Transactional
    fun moveDefiningTheme(definingThemeOrderDetails: DefiningThemeOrderDetails): DefiningTheme {
        val definingThemeId = definingThemeOrderDetails.definingThemeId
        val targetOrderNumber = definingThemeOrderDetails.orderNumber

        val definingTheme = definingThemesRepository.findByIdOrNull(definingThemeId)
            ?: throw IllegalArgumentException("DefiningTheme with id $definingThemeId is not found")

        val categoryId = definingTheme.categoryId
        val maxOrderNumber = definingThemesRepository.findMaxOrderNumber(categoryId)
            ?: definingTheme.orderNumber

        if (targetOrderNumber > maxOrderNumber) {
            throw IllegalArgumentException("OrderNumber must be between $ID_MIN and $maxOrderNumber")
        }

        val currentOrderNumber = definingTheme.orderNumber
        if (targetOrderNumber == currentOrderNumber) return definingTheme

        val lowOrderNumber = min(currentOrderNumber, targetOrderNumber)
        val highOrderNumber = max(currentOrderNumber, targetOrderNumber)
        val shift = maxOrderNumber * SHIFT_MULTIPLIER

        definingThemesRepository.shiftOrderNumbersOutOfRange(
            categoryId = categoryId,
            lowOrderNumber = lowOrderNumber,
            highOrderNumber = highOrderNumber,
            shift = shift
        )
        definingThemesRepository.applyShiftedOrderNumbers(
            categoryId = categoryId,
            definingThemeId = definingThemeId,
            targetOrderNumber = targetOrderNumber,
            lowOrderNumber = lowOrderNumber,
            highOrderNumber = highOrderNumber,
            shift = shift,
            step = if (targetOrderNumber < currentOrderNumber) 1 else -1
        )

        return definingThemesRepository.findByIdOrNull(definingThemeId)!!
    }

    private companion object {
        const val SHIFT_MULTIPLIER = 2
    }
}