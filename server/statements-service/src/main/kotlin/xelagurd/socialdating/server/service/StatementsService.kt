package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.repository.StatementsRepository

@Service
class StatementsService(
    private val statementsRepository: StatementsRepository,
    private val kafkaProducer: StatementsKafkaProducer
) {

    fun getStatements(definingThemeIds: List<Int>): List<Statement> {
        return statementsRepository.findAllByDefiningThemeIdIn(definingThemeIds).takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("Statements didn't found for definingThemeIds")
    }

    fun addStatement(statementDetails: StatementDetails): Statement {
        return statementsRepository.save(statementDetails.toStatement())
    }

    fun addStatementReaction(statementId: Int, statementReactionDetails: StatementReactionDetails) {
        kafkaProducer.sendStatementReaction(statementReactionDetails)
    }
}