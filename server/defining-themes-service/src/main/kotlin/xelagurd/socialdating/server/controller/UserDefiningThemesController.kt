package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.server.security.BearerAuth
import xelagurd.socialdating.server.service.UserDefiningThemesService
import xelagurd.socialdating.server.utils.DataUtils.responseEntities

@RestController
@RequestMapping(path = ["/defining-themes/users"], produces = ["application/json"])
class UserDefiningThemesController(
    private val userDefiningThemesService: UserDefiningThemesService
) {

    @BearerAuth
    @GetMapping
    fun getUserDefiningThemes(@RequestParam userId: Int) =
        responseEntities { userDefiningThemesService.getUserDefiningThemes(userId) }

}