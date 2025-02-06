package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.details.UserDefiningThemeDetails
import xelagurd.socialdating.server.service.UserDefiningThemesService

@RestController
@RequestMapping(path = ["/api/v1/defining-themes/users"], produces = ["application/json"])
class UserDefiningThemesController(
    private val userDefiningThemesService: UserDefiningThemesService
) {

    @GetMapping
    fun getUserDefiningThemes(@RequestParam userCategoryIds: List<Int>): List<UserDefiningTheme> {
        return userDefiningThemesService.getUserDefiningThemes(userCategoryIds)
    }

    // TODO: Add admin privileges
    @PostMapping
    fun addUserDefiningTheme(@RequestBody userDefiningThemeDetails: UserDefiningThemeDetails): UserDefiningTheme {
        return userDefiningThemesService.addUserDefiningTheme(userDefiningThemeDetails)
    }
}