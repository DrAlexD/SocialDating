package xelagurd.socialdating.server.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import jakarta.validation.Valid
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.security.AdminAccess
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

    @AdminAccess
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addDefiningTheme(@RequestBody @Valid definingThemeDetails: DefiningThemeDetails): DefiningTheme {
        return definingThemesService.addDefiningTheme(definingThemeDetails)
    }
}