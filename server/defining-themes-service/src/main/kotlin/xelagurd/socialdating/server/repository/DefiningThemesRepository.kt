package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xelagurd.socialdating.server.model.DefiningTheme

interface DefiningThemesRepository : JpaRepository<DefiningTheme, Int> {

    @Query(
        """
        select *
        from defining_themes
        where (coalesce(:definingThemeIds) is null or id in (:definingThemeIds))
          and (:categoryId is null or category_id = :categoryId)
        """,
        nativeQuery = true
    )
    fun findAllByIdsAndCategoryId(definingThemeIds: List<Int>?, categoryId: Int?): List<DefiningTheme>

    @Query(
        """
        select max(number_in_category)
        from defining_themes
        where category_id = :categoryId
        """,
        nativeQuery = true
    )
    fun findMaxNumberInCategory(categoryId: Int): Int?

}