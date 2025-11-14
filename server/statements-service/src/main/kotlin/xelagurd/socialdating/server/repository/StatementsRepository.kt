package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xelagurd.socialdating.server.model.Statement

interface StatementsRepository : JpaRepository<Statement, Int> {
    @Query(
        """
        select stm.*
        from statements stm
        left join user_statements ustm on stm.id = ustm.statement_id and ustm.user_id = :userId
        where defining_theme_id in (:definingThemeIds) and ustm.id is null
        """,
        nativeQuery = true
    )
    fun findUnreactedStatements(userId: Int, definingThemeIds: List<Int>): List<Statement>
}