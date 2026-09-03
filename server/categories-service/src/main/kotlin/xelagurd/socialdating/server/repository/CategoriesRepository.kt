package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import xelagurd.socialdating.server.model.Category

interface CategoriesRepository : JpaRepository<Category, Int> {

    @Query(
        """
        select *
        from categories
        where coalesce(:categoryIds) is null or id in (:categoryIds)
        order by order_number
        """,
        nativeQuery = true
    )
    fun findAllByIds(categoryIds: List<Int>?): List<Category>

    @Query(
        """
        select max(order_number)
        from categories
        """,
        nativeQuery = true
    )
    fun findMaxOrderNumber(): Int?

    @Modifying
    @Query(
        """
        update categories
        set order_number = order_number + :shift
        where order_number between :lowOrderNumber and :highOrderNumber
        """,
        nativeQuery = true
    )
    fun shiftOrderNumbersOutOfRange(lowOrderNumber: Int, highOrderNumber: Int, shift: Int)

    @Modifying(clearAutomatically = true)
    @Query(
        """
        update categories
        set order_number = case
                             when id = :categoryId then :targetOrderNumber
                             else order_number - :shift + :step
                           end
        where order_number between :lowOrderNumber + :shift and :highOrderNumber + :shift
        """,
        nativeQuery = true
    )
    fun applyShiftedOrderNumbers(
        categoryId: Int,
        targetOrderNumber: Int,
        lowOrderNumber: Int,
        highOrderNumber: Int,
        shift: Int,
        step: Int
    )

}