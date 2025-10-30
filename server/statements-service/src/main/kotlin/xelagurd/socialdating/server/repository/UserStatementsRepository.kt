package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xelagurd.socialdating.server.model.UserStatement

interface UserStatementsRepository : JpaRepository<UserStatement, Int> {
    @Query(
        """
        select ustm.*
        from user_statements ustm
        join statements stm on ustm.statement_id = stm.id and ustm.user_id = :userId
        where defining_theme_id in (:definingThemeIds)
        """,
        nativeQuery = true
    )
    fun findAllByUserIdAndDefiningThemeIds(userId: Int, definingThemeIds: List<Int>): List<UserStatement>
}