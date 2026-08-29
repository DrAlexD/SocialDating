package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xelagurd.socialdating.server.model.Statement

interface StatementsRepository : JpaRepository<Statement, Int> {
    @Query(
        """
        select distinct stm.*
        from statements stm
        join statement_defining_themes sdt on stm.id = sdt.statement_id
        left join user_statements ustm on stm.id = ustm.statement_id and ustm.user_id = :currentUserId
        where sdt.defining_theme_id in (:definingThemeIds) and ustm.id is null
        order by stm.id
        """,
        nativeQuery = true
    )
    fun findUnreactedStatements(currentUserId: Int, definingThemeIds: List<Int>): List<Statement>
}
