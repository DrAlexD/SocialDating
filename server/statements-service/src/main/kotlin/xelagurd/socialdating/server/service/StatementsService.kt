package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.repository.StatementsRepository

@Service
class StatementsService(
    private val statementsRepository: StatementsRepository
) {

    fun getStatements(userId: Int, definingThemeIds: List<Int>) =
        statementsRepository.findUnreactedStatements(userId, definingThemeIds)

    fun addStatement(statementDetails: StatementDetails) =
        statementsRepository.save(statementDetails.toStatement())
}