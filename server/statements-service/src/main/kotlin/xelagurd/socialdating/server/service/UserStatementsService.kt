package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
            ?: throw NoDataFoundException("UserStatements didn't found for userId $userId and definingThemeIds $definingThemeIds")
    }

    fun addUserStatement(userStatementDetails: UserStatementDetails): UserStatement {
        return userStatementsRepository.save(userStatementDetails.toUserStatement())
    }

    @Transactional
    fun handleStatementReaction(statementReactionDetails: StatementReactionDetails): UserStatement {
        val userStatementDetails = UserStatementDetails(
            reactionType = statementReactionDetails.reactionType,
            userId = statementReactionDetails.userId,
            statementId = statementReactionDetails.statementId
        )
        val userStatement = userStatementsRepository.save(userStatementDetails.toUserStatement())

        kafkaProducer.updateUserCategory(
            statementReactionDetails.toUserCategoryUpdateDetails()
        )

        return userStatement
    }
}