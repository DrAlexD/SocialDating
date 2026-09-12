package xelagurd.socialdating.server.repository

import org.springframework.data.domain.Limit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import xelagurd.socialdating.server.model.Statement

interface StatementsRepository : JpaRepository<Statement, Int> {
    @Query(
        """
        select stm.*
        from statements stm
        where exists (
            select 1
            from statement_defining_themes sdt
            where sdt.statement_id = stm.id and sdt.defining_theme_id in (:definingThemeIds)
        )
        and not exists (
            select 1
            from user_statements ustm
            where ustm.statement_id = stm.id and ustm.user_id = :currentUserId
        )
        and md5(stm.id || cast(:seed as text)) > :lastOrderKey
        order by md5(stm.id || cast(:seed as text))
        """,
        nativeQuery = true
    )
    fun findUnreactedStatements(
        currentUserId: Int,
        definingThemeIds: List<Int>,
        seed: String,
        lastOrderKey: String,
        limit: Limit
    ): List<Statement>
}
