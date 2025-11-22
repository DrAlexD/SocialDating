package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.CategoryWithData

interface UserCategoriesRepository : JpaRepository<UserCategory, Int> {

    fun findAllByUserId(userId: Int): List<UserCategory>

    fun findByUserIdAndCategoryId(userId: Int, categoryId: Int): UserCategory?

    @Query(
        """
        with current_user_categories as
               (select *
                from user_categories
                where user_id = :userId
                  and (maintained is not null or not_maintained is not null)
                  and (coalesce(:categoryIds) is null or category_id in (:categoryIds)))
        select uc.category_id as id,
               c.name,
               uc.maintained,
               uc.not_maintained
        from current_user_categories uc
          left join categories c on uc.category_id = c.id
        """,
        nativeQuery = true
    )
    fun findCurrentUserCategories(userId: Int, categoryIds: List<Int>?): List<CategoryWithData>

    @Query(
        """
        select *
        from user_categories
        where user_id != :currentUserId
          and (:anotherUserId is null or user_id = :anotherUserId)
          and (maintained is not null or not_maintained is not null)
          and category_id in (:categoryIds)
        """,
        nativeQuery = true
    )
    fun findAnotherUsersCategories(currentUserId: Int, anotherUserId: Int?, categoryIds: List<Int>): List<UserCategory>

}