package xelagurd.socialdating.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.dto.UserCategory
import xelagurd.socialdating.dto.UserCategoryDetails
import xelagurd.socialdating.repository.UserCategoriesRepository

@Service
class UserCategoriesService(
    private val userCategoriesRepository: UserCategoriesRepository
) {

    fun getUserCategory(userId: Int, categoryId: Int): UserCategory? {
        return userCategoriesRepository.findByUserIdAndCategoryId(userId, categoryId)
    }

    fun getUserCategories(userId: Int): Iterable<UserCategory> {
        return userCategoriesRepository.findAllByUserId(userId)
    }

    fun addUserCategory(userCategoryDetails: UserCategoryDetails): UserCategory {
        return userCategoriesRepository.save(userCategoryDetails.toUserCategory())
    }

    fun addUserCategory(userCategory: UserCategory): UserCategory {
        return userCategoriesRepository.save(userCategory)
    }
}