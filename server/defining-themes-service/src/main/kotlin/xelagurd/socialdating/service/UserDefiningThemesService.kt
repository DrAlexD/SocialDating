package xelagurd.socialdating.service

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import xelagurd.socialdating.dto.UserDefiningTheme
import xelagurd.socialdating.dto.UserDefiningThemeDetails
import xelagurd.socialdating.repository.UserDefiningThemesRepository

@Service
class UserDefiningThemesService(
    private val userDefiningThemesRepository: UserDefiningThemesRepository
) {

    fun getUserDefiningThemes(userCategoryIds: List<Int>): Iterable<UserDefiningTheme> {
        return userDefiningThemesRepository.findAllByUserCategoryIdIn(userCategoryIds)
    }

    fun addUserDefiningTheme(@RequestBody userDefiningThemeDetails: UserDefiningThemeDetails): UserDefiningTheme {
        return userDefiningThemesRepository.save(userDefiningThemeDetails.toUserDefiningTheme())
    }
}