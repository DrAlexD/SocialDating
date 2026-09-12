package xelagurd.socialdating.server.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xelagurd.socialdating.server.model.DefaultDataProperties.PAGE_SIZE_DEFAULT
import xelagurd.socialdating.server.security.BearerAuth
import xelagurd.socialdating.server.service.UserCategoriesService
import xelagurd.socialdating.server.utils.DataUtils.responseEntities
import xelagurd.socialdating.server.utils.DataUtils.responsePage

@RestController
@RequestMapping(path = ["/categories/users"], produces = ["application/json"])
class UserCategoriesController(
    private val userCategoriesService: UserCategoriesService
) {

    @BearerAuth
    @GetMapping
    fun getUserCategories(@RequestParam userId: Int) =
        responseEntities { userCategoriesService.getUserCategories(userId) }

    @BearerAuth
    @GetMapping("/similar-users")
    fun getSimilarUsers(
        @RequestParam currentUserId: Int,
        @RequestParam(required = false) categoryIds: List<Int>?,
        @RequestParam(required = false) cursor: String?,
        @RequestParam(defaultValue = "$PAGE_SIZE_DEFAULT") size: Int
    ) =
        responsePage { userCategoriesService.getSimilarUsers(currentUserId, categoryIds, cursor, size) }

    @BearerAuth
    @GetMapping("/detailed-similar-user")
    fun getDetailedSimilarUser(
        @RequestParam currentUserId: Int,
        @RequestParam anotherUserId: Int
    ) =
        userCategoriesService.getDetailedSimilarUser(currentUserId, anotherUserId)

}