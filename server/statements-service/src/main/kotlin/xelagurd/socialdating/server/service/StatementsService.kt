package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.repository.StatementsRepository

@Service
class StatementsService(
    private val statementsRepository: StatementsRepository
) {

    fun getStatements(userId: Int, definingThemeIds: List<Int>): List<Statement> {
        return statementsRepository.findUnreactedStatements(userId, definingThemeIds).takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("Statements didn't found for userId and definingThemeIds")
    }

    fun addStatement(statementDetails: StatementDetails): Statement {
        return statementsRepository.save(statementDetails.toStatement())
    }
}