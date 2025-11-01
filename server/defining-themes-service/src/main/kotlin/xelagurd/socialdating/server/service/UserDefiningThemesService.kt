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

    fun getUserDefiningThemes(userId: Int): List<UserDefiningTheme> {
        return userDefiningThemesRepository.findAllByUserId(userId).takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("UserDefiningThemes didn't found for userId: $userId")
    }

    fun addUserDefiningTheme(userDefiningThemeDetails: UserDefiningThemeDetails): UserDefiningTheme {
        return userDefiningThemesRepository.save(userDefiningThemeDetails.toUserDefiningTheme())
    }

    fun addUserDefiningTheme(userDefiningTheme: UserDefiningTheme): UserDefiningTheme {
        return userDefiningThemesRepository.save(userDefiningTheme)
    }

    fun getUserDefiningTheme(userId: Int, definingThemeId: Int): UserDefiningTheme? {
        return userDefiningThemesRepository.findByUserIdAndDefiningThemeId(userId, definingThemeId)
    }
}