package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import xelagurd.socialdating.server.model.DefiningTheme

interface DefiningThemesRepository : JpaRepository<DefiningTheme, Int> {

    @Query(
        """
        select *
        from defining_themes
        where (coalesce(:definingThemeIds) is null or id in (:definingThemeIds))
          and (:categoryId is null or category_id = :categoryId)
        order by order_number, category_id
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

    @Query(
        """
        select max(order_number)
        from defining_themes
        where category_id = :categoryId
        """,
        nativeQuery = true
    )
    fun findMaxOrderNumber(categoryId: Int): Int?

    @Modifying
    @Query(
        """
        update defining_themes
        set order_number = order_number + :shift
        where category_id = :categoryId
          and order_number between :lowOrderNumber and :highOrderNumber
        """,
        nativeQuery = true
    )
    fun shiftOrderNumbersOutOfRange(categoryId: Int, lowOrderNumber: Int, highOrderNumber: Int, shift: Int)

    @Modifying(clearAutomatically = true)
    @Query(
        """
        update defining_themes
        set order_number = case
                             when id = :definingThemeId then :targetOrderNumber
                             else order_number - :shift + :step
                           end
        where category_id = :categoryId
          and order_number between :lowOrderNumber + :shift and :highOrderNumber + :shift
        """,
        nativeQuery = true
    )
    fun applyShiftedOrderNumbers(
        categoryId: Int,
        definingThemeId: Int,
        targetOrderNumber: Int,
        lowOrderNumber: Int,
        highOrderNumber: Int,
        shift: Int,
        step: Int
    )

}