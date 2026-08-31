package xelagurd.socialdating.server.service

import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.common.UserDefiningThemesUpdateDetails
import xelagurd.socialdating.server.repository.StatementDefiningThemesRepository
import xelagurd.socialdating.server.repository.UserStatementsRepository
import xelagurd.socialdating.server.utils.SecurityUtils.checkCurrentUserAuth

@Service
class UserStatementsService(
    private val userStatementsRepository: UserStatementsRepository,
    private val statementDefiningThemesRepository: StatementDefiningThemesRepository,
    private val kafkaProducer: StatementsKafkaProducer
) {

    fun processStatementReaction(statementReactionDetails: StatementReactionDetails) {
        checkCurrentUserAuth(statementReactionDetails.userId)

        val definingThemes = statementDefiningThemesRepository
            .findAllByStatementId(statementReactionDetails.statementId)

        userStatementsRepository.save(statementReactionDetails.toUserStatement())

        kafkaProducer.updateUserDefiningThemes(
            UserDefiningThemesUpdateDetails(
                userId = statementReactionDetails.userId,
                reactionType = statementReactionDetails.reactionType,
                definingThemes = definingThemes.map { it.toDefiningThemeReactionDetails() }
            )
        )
    }

}
