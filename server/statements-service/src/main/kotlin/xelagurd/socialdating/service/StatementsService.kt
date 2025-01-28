package xelagurd.socialdating.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import xelagurd.socialdating.dto.Statement
import xelagurd.socialdating.dto.StatementDetails
import xelagurd.socialdating.dto.StatementReactionDetails
import xelagurd.socialdating.repository.StatementsRepository

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
        val statement = this.getStatement(statementId)

        statement?.let {
            val statementReaction = statementReactionDetails.toStatementReaction(
                it.definingThemeId!!,
                it.isSupportDefiningTheme!!
            )

            kafkaProducer.sendStatementReaction(statementReaction)
        }
    }
}