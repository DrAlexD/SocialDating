package xelagurd.socialdating.service

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import xelagurd.socialdating.dto.DefiningTheme
import xelagurd.socialdating.dto.DefiningThemeDetails
import xelagurd.socialdating.repository.DefiningThemesRepository

@Service
class DefiningThemesService(
    private val definingThemesRepository: DefiningThemesRepository
) {

    fun getDefiningThemes(categoryIds: List<Int>): Iterable<DefiningTheme> {
        return definingThemesRepository.findAllByCategoryIdIn(categoryIds)
    }

    fun addDefiningTheme(@RequestBody definingThemeDetails: DefiningThemeDetails): DefiningTheme {
        return definingThemesRepository.save(definingThemeDetails.toDefiningTheme())
    }
}