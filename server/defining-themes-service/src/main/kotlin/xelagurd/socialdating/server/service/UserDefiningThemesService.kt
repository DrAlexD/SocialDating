package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.details.UserDefiningThemeDetails
import xelagurd.socialdating.server.repository.UserDefiningThemesRepository

@Service
class UserDefiningThemesService(
    private val userDefiningThemesRepository: UserDefiningThemesRepository
) {

    fun getUserDefiningThemes(userCategoryIds: List<Int>): List<UserDefiningTheme> {
        return userDefiningThemesRepository.findAllByUserCategoryIdIn(userCategoryIds).takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("UserDefiningThemes didn't found for userCategoryIds")
    }

    fun addUserDefiningTheme(userDefiningThemeDetails: UserDefiningThemeDetails): UserDefiningTheme {
        return userDefiningThemesRepository.save(userDefiningThemeDetails.toUserDefiningTheme())
    }

    fun addUserDefiningTheme(userDefiningTheme: UserDefiningTheme): UserDefiningTheme {
        return userDefiningThemesRepository.save(userDefiningTheme)
    }

    fun getUserDefiningTheme(userCategoryId: Int, definingThemeId: Int): UserDefiningTheme? {
        return userDefiningThemesRepository.findByUserCategoryIdAndDefiningThemeId(userCategoryId, definingThemeId)
    }
}