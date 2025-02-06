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

    fun getDefiningThemes(categoryIds: List<Int>): List<DefiningTheme> {
        return definingThemesRepository.findAllByCategoryIdIn(categoryIds).takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("DefiningThemes didn't found for categoryIds")
    }

    fun addDefiningTheme(definingThemeDetails: DefiningThemeDetails): DefiningTheme {
        return definingThemesRepository.save(definingThemeDetails.toDefiningTheme())
    }
}