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
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.details.UserCategoryDetails
import xelagurd.socialdating.server.security.AdminAccess
import xelagurd.socialdating.server.service.UserCategoriesService

@RestController
@RequestMapping(path = ["/api/v1/categories/users"], produces = ["application/json"])
class UserCategoriesController(
    private val userCategoriesService: UserCategoriesService
) {

    @GetMapping
    fun getUserCategories(@RequestParam userId: Int): List<UserCategory> {
        return userCategoriesService.getUserCategories(userId)
    }

    @AdminAccess
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addUserCategory(@RequestBody @Valid userCategoryDetails: UserCategoryDetails): UserCategory {
        return userCategoriesService.addUserCategory(userCategoryDetails)
    }
}