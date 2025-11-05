package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.repository.DefiningThemesRepository

@Service
class DefiningThemesService(
    private val definingThemesRepository: DefiningThemesRepository
) {

    fun getDefiningThemes(categoryId: Int? = null): List<DefiningTheme> {
        val definingThemes = categoryId?.let { definingThemesRepository.findAllByCategoryId(it) }
            ?: definingThemesRepository.findAll()

        return definingThemes.takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("DefiningThemes didn't found for categoryId: $categoryId")
    }

    fun addDefiningTheme(definingThemeDetails: DefiningThemeDetails): DefiningTheme {
        return definingThemesRepository.save(definingThemeDetails.toDefiningTheme())
    }
}