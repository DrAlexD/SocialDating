package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xelagurd.socialdating.server.model.additional.StatementWithDefiningThemes
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.repository.StatementDefiningThemesRepository
import xelagurd.socialdating.server.repository.StatementsRepository
import xelagurd.socialdating.server.utils.SecurityUtils.checkCurrentUserAuth

@Service
class StatementsService(
    private val statementsRepository: StatementsRepository,
    private val statementDefiningThemesRepository: StatementDefiningThemesRepository
) {

    fun getStatements(currentUserId: Int, definingThemeIds: List<Int>): List<StatementWithDefiningThemes> {
        checkCurrentUserAuth(currentUserId)

        val statements = statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds)

        if (statements.isEmpty()) return emptyList()

        val definingThemesByStatementId = statementDefiningThemesRepository
            .findAllByStatementIdIn(statements.map { it.id!! })
            .groupBy { it.statementId }

        return statements.map {
            it.toStatementWithDefiningThemes(definingThemesByStatementId[it.id] ?: emptyList())
        }
    }

    @Transactional
    fun addStatement(statementDetails: StatementDetails): StatementWithDefiningThemes {
        val definingThemeIds = statementDetails.definingThemes.map { it.definingThemeId }

        if (definingThemeIds.size != definingThemeIds.toSet().size) {
            throw IllegalArgumentException("Statement has duplicated defining themes")
        }

        val statement = statementsRepository.save(statementDetails.toStatement())
        val definingThemes = statementDefiningThemesRepository
            .saveAll(statementDetails.toStatementDefiningThemes(statement.id!!))

        return statement.toStatementWithDefiningThemes(definingThemes)
    }
}
