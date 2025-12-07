package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xelagurd.socialdating.server.model.Category

interface CategoriesRepository : JpaRepository<Category, Int> {

    @Query(
        """
        select *
        from categories
        where coalesce(:categoryIds) is null or id in (:categoryIds)
        """,
        nativeQuery = true
    )
    fun findAllByIds(categoryIds: List<Int>?): List<Category>

}