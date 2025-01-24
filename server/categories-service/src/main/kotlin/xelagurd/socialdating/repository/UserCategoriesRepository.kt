package xelagurd.socialdating.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.dto.UserCategory

interface UserCategoriesRepository : CrudRepository<UserCategory, Int> {
    fun findAllByUserId(userId: Int): Iterable<UserCategory>

    fun findByUserIdAndCategoryId(userId: Int, categoryId: Int): UserCategory?
}