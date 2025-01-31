package xelagurd.socialdating.server.repository

import org.springframework.data.repository.CrudRepository
import xelagurd.socialdating.server.model.UserCategory

interface UserCategoriesRepository : CrudRepository<UserCategory, Int> {
    fun findAllByUserId(userId: Int): Iterable<UserCategory>

    fun findByUserIdAndCategoryId(userId: Int, categoryId: Int): UserCategory?
}