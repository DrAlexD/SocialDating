package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.SimilarUser
import xelagurd.socialdating.server.model.additional.UserWithSimilarity
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
    @GetMapping("/users-similarity")
    fun getUsersWithSimilarity(
        @RequestParam userId: Int,
        @RequestParam(required = false) categoryIds: List<Int>?
    ): List<UserWithSimilarity> {
        return userCategoriesService.getUsersWithSimilarity(userId, categoryIds)
    }

    @Operation(security = [SecurityRequirement("bearerAuth")])
    @GetMapping("/similar-user")
    fun getSimilarUser(
        @RequestParam currentUserId: Int,
        @RequestParam anotherUserId: Int
    ): SimilarUser {
        return userCategoriesService.getSimilarUser(currentUserId, anotherUserId)
    }

}