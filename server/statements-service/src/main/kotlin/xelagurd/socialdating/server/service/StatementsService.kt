package xelagurd.socialdating.server.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.repository.StatementsRepository

@Service
class StatementsService(
    private val statementsRepository: StatementsRepository,
    private val kafkaProducer: StatementsKafkaProducer
) {

    fun getStatement(statementId: Int): Statement? {
        return statementsRepository.findByIdOrNull(statementId)
    }

    fun getStatements(definingThemeIds: List<Int>): Iterable<Statement> {
        return statementsRepository.findAllByDefiningThemeIdIn(definingThemeIds)
    }

    fun addStatement(statementDetails: StatementDetails): Statement {
        return statementsRepository.save(statementDetails.toStatement())
    }

    fun addStatementReaction(statementId: Int, statementReactionDetails: StatementReactionDetails) {
        kafkaProducer.sendStatementReaction(statementReactionDetails)
    }
}