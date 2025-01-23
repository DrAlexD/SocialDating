package xelagurd.socialdating.service

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import xelagurd.socialdating.dto.UserCategory
import xelagurd.socialdating.dto.UserCategoryDetails
import xelagurd.socialdating.repository.UserCategoriesRepository

@Service
class UserCategoriesService(
    private val userCategoriesRepository: UserCategoriesRepository
) {

    fun getUserCategories(userId: Int): Iterable<UserCategory> {
        return userCategoriesRepository.findAllByUserId(userId)
    }

    fun addUserCategory(@RequestBody userCategoryDetails: UserCategoryDetails): UserCategory {
        return userCategoriesRepository.save(userCategoryDetails.toUserCategory())
    }
}