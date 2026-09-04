package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.dto.UserDefiningThemeDto
import xelagurd.socialdating.server.repository.UserDefiningThemesRepository

@Service
class UserDefiningThemesService(
    private val userDefiningThemesRepository: UserDefiningThemesRepository
) {

    fun getUserDefiningThemes(userId: Int): List<UserDefiningThemeDto> =
        userDefiningThemesRepository.findAllByUserId(userId).map { it.toUserDefiningThemeDto() }

    fun addUserDefiningTheme(userDefiningTheme: UserDefiningTheme) =
        userDefiningThemesRepository.save(userDefiningTheme)

    fun getUserDefiningTheme(userId: Int, definingThemeId: Int) =
        userDefiningThemesRepository.findByUserIdAndDefiningThemeId(userId, definingThemeId)
}