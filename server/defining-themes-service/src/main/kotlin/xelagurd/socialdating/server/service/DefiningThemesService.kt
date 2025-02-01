package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.repository.DefiningThemesRepository

@Service
class DefiningThemesService(
    private val definingThemesRepository: DefiningThemesRepository
) {

    fun getDefiningThemes(categoryIds: List<Int>): Iterable<DefiningTheme> {
        return definingThemesRepository.findAllByCategoryIdIn(categoryIds)
    }

    fun addDefiningTheme(definingTheme: DefiningTheme): DefiningTheme {
        return definingThemesRepository.save(definingTheme)
    }
}