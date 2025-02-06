package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import xelagurd.socialdating.server.model.UserCategory

interface UserCategoriesRepository : JpaRepository<UserCategory, Int> {
    fun findAllByUserId(userId: Int): List<UserCategory>

    fun findByUserIdAndCategoryId(userId: Int, categoryId: Int): UserCategory?
}