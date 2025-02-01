package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.repository.UserCategoriesRepository

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

    fun addUserCategory(userCategory: UserCategory): UserCategory {
        return userCategoriesRepository.save(userCategory)
    }
}