package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.DetailedSimilarUser
import xelagurd.socialdating.server.model.additional.SimilarUser
import xelagurd.socialdating.server.service.UserCategoriesService

@RestController
@RequestMapping(path = ["/categories/users"], produces = ["application/json"])
class UserCategoriesController(
    private val userCategoriesService: UserCategoriesService
) {

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @GetMapping
    fun getUserCategories(@RequestParam userId: Int): List<UserCategory> {
        return userCategoriesService.getUserCategories(userId)
    }

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @GetMapping("/similar-users")
    fun getSimilarUsers(
        @RequestParam userId: Int,
        @RequestParam(required = false) categoryIds: List<Int>?
    ): List<SimilarUser> {
        return userCategoriesService.getSimilarUsers(userId, categoryIds)
    }

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @GetMapping("/detailed-similar-user")
    fun getDetailedSimilarUser(
        @RequestParam currentUserId: Int,
        @RequestParam anotherUserId: Int
    ): DetailedSimilarUser {
        return userCategoriesService.getDetailedSimilarUser(currentUserId, anotherUserId)
    }

}