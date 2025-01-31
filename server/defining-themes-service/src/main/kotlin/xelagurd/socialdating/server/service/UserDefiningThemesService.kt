package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.details.UserDefiningThemeDetails
import xelagurd.socialdating.server.repository.UserDefiningThemesRepository

@Service
class UserDefiningThemesService(
    private val userDefiningThemesRepository: UserDefiningThemesRepository
) {

    fun getUserDefiningTheme(userCategoryId: Int, definingThemeId: Int): UserDefiningTheme? {
        return userDefiningThemesRepository.findByUserCategoryIdAndDefiningThemeId(userCategoryId, definingThemeId)
    }

    fun getUserDefiningThemes(userCategoryIds: List<Int>): Iterable<UserDefiningTheme> {
        return userDefiningThemesRepository.findAllByUserCategoryIdIn(userCategoryIds)
    }

    fun addUserDefiningTheme(userDefiningThemeDetails: UserDefiningThemeDetails): UserDefiningTheme {
        return userDefiningThemesRepository.save(userDefiningThemeDetails.toUserDefiningTheme())
    }

    fun addUserDefiningTheme(userDefiningTheme: UserDefiningTheme): UserDefiningTheme {
        return userDefiningThemesRepository.save(userDefiningTheme)
    }
}