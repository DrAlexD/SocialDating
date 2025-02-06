package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.service.DefiningThemesService

@RestController
@RequestMapping(path = ["/api/v1/defining-themes"], produces = ["application/json"])
class DefiningThemesController(
    private val definingThemesService: DefiningThemesService
) {

    @GetMapping
    fun getDefiningThemes(@RequestParam categoryIds: List<Int>): List<DefiningTheme> {
        return definingThemesService.getDefiningThemes(categoryIds)
    }

    // TODO: Add admin privileges
    @PostMapping
    fun addDefiningTheme(@RequestBody definingThemeDetails: DefiningThemeDetails): DefiningTheme {
        return definingThemesService.addDefiningTheme(definingThemeDetails)
    }
}