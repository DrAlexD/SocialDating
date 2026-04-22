package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.repository.StatementsRepository
import xelagurd.socialdating.server.utils.SecurityUtils.checkCurrentUserAuth

@Service
class StatementsService(
    private val statementsRepository: StatementsRepository
) {

    fun getStatements(currentUserId: Int, definingThemeIds: List<Int>): List<Statement> {
        checkCurrentUserAuth(currentUserId)

        return statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds)
    }

    fun addStatement(statementDetails: StatementDetails) =
        statementsRepository.save(statementDetails.toStatement())
}