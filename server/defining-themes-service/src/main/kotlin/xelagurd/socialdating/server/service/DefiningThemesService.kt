package xelagurd.socialdating.server.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.repository.DefiningThemesRepository

@Service
class DefiningThemesService(
    private val definingThemesRepository: DefiningThemesRepository
) {

    fun getDefiningThemes(categoryId: Int? = null) =
        categoryId?.let { definingThemesRepository.findAllByCategoryId(it) }
            ?: definingThemesRepository.findAll()

    fun getDefiningTheme(definingThemeId: Int) =
        definingThemesRepository.findByIdOrNull(definingThemeId)

    @Transactional
    fun addDefiningTheme(definingThemeDetails: DefiningThemeDetails): DefiningTheme {
        val numberInCategory = definingThemesRepository.findMaxNumberInCategory(definingThemeDetails.categoryId)
            ?.plus(1)
        return definingThemesRepository.save(definingThemeDetails.toDefiningTheme(numberInCategory))
    }
}