package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.UserStatementDetails
import xelagurd.socialdating.server.repository.UserStatementsRepository

@Service
class UserStatementsService(
    private val userStatementsRepository: UserStatementsRepository,
    private val kafkaProducer: StatementsKafkaProducer
) {

    fun getUserStatements(userId: Int, definingThemeIds: List<Int>): List<UserStatement> {
        return userStatementsRepository.findAllByUserIdAndDefiningThemeIds(userId, definingThemeIds).takeIf { it.isNotEmpty() }
            ?: throw NoDataFoundException("UserStatements didn't found for userId: $userId")
    }

    fun addUserStatement(userStatementDetails: UserStatementDetails): UserStatement {
        return userStatementsRepository.save(userStatementDetails.toUserStatement())
    }

    fun addStatementReaction(statementReactionDetails: StatementReactionDetails): UserStatement {
        val userStatementDetails = UserStatementDetails(
            reactionType = statementReactionDetails.reactionType,
            userId = statementReactionDetails.userOrUserCategoryId,
            statementId = statementReactionDetails.statementId
        )
        val userStatement = userStatementsRepository.save(userStatementDetails.toUserStatement())

        kafkaProducer.sendStatementReaction(statementReactionDetails)

        return userStatement
    }
}