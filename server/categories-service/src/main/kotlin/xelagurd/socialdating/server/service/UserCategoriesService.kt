package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.details.UserCategoryDetails
import xelagurd.socialdating.server.repository.UserCategoriesRepository

@Service
class UserCategoriesService(
    private val userCategoriesRepository: UserCategoriesRepository
) {

    fun getUserCategories(userId: Int): List<UserCategory> {
        return userCategoriesRepository.findAllByUserId(userId).takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("UserCategories didn't found for userId: $userId")
    }

    fun addUserCategory(userCategoryDetails: UserCategoryDetails): UserCategory {
        return userCategoriesRepository.save(userCategoryDetails.toUserCategory())
    }

    fun addUserCategory(userCategory: UserCategory): UserCategory {
        return userCategoriesRepository.save(userCategory)
    }

    fun getUserCategory(userId: Int, categoryId: Int): UserCategory? {
        return userCategoriesRepository.findByUserIdAndCategoryId(userId, categoryId)
    }
}