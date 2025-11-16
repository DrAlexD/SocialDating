package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.repository.UserStatementsRepository

@Service
class UserStatementsService(
    private val userStatementsRepository: UserStatementsRepository,
    private val kafkaProducer: StatementsKafkaProducer
) {

    fun processStatementReaction(statementReactionDetails: StatementReactionDetails) {
        userStatementsRepository.save(statementReactionDetails.toUserStatement())

        kafkaProducer.updateUserCategory(
            statementReactionDetails.toUserCategoryUpdateDetails()
        )
    }

}