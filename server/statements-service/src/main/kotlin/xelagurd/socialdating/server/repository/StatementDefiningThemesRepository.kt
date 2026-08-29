package xelagurd.socialdating.server.repository

import org.springframework.data.jpa.repository.JpaRepository
import xelagurd.socialdating.server.model.StatementDefiningTheme

interface StatementDefiningThemesRepository : JpaRepository<StatementDefiningTheme, Int> {
    fun findAllByStatementId(statementId: Int): List<StatementDefiningTheme>

    fun findAllByStatementIdIn(statementIds: List<Int>): List<StatementDefiningTheme>
}
