package xelagurd.socialdating.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.dto.UserCategory
import xelagurd.socialdating.dto.UserCategoryDetails
import xelagurd.socialdating.service.UserCategoriesService

@RestController
@RequestMapping(path = ["/api/v1/categories/users"], produces = ["application/json"])
class UserCategoriesController(
    private val userCategoriesService: UserCategoriesService
) {

    @GetMapping("/{id}")
    fun getUserCategories(@PathVariable("id") userId: Int): Iterable<UserCategory> {
        return userCategoriesService.getUserCategories(userId)
    }

    // TODO: Add admin privileges
    @PostMapping
    fun addUserCategory(@RequestBody userCategoryDetails: UserCategoryDetails): UserCategory {
        return userCategoriesService.addUserCategory(userCategoryDetails)
    }
}