package xelagurd.socialdating.server.service

import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType

@Profile("!test")
@Service
class DefiningThemesKafkaConsumer(
    private val userDefiningThemesService: UserDefiningThemesService
) {

    @KafkaListener(topics = ["statement-reaction-to-user-defining-theme-topic"], groupId = "defining-themes-group")
    fun consumeStatementReaction(statementReactionDetails: StatementReactionDetails) {
        var userDefiningTheme = userDefiningThemesService.getUserDefiningTheme(
            statementReactionDetails.userOrUserCategoryId,
            statementReactionDetails.definingThemeId
        )

        var diff = when (statementReactionDetails.reactionType) {
            StatementReactionType.FULL_NO_MAINTAIN -> -10
            StatementReactionType.PART_NO_MAINTAIN -> -5
            StatementReactionType.NOT_SURE -> 0
            StatementReactionType.PART_MAINTAIN -> 5
            StatementReactionType.FULL_MAINTAIN -> 10
        }

        if (!statementReactionDetails.isSupportDefiningTheme) {
            diff = -diff
        }

        if (userDefiningTheme != null) {
            userDefiningTheme.value = userDefiningTheme.value + diff

            userDefiningTheme.interest = userDefiningTheme.interest + 5
        } else {
            userDefiningTheme = UserDefiningTheme(
                value = 50 + diff,
                interest = 5,
                userCategoryId = statementReactionDetails.userOrUserCategoryId,
                definingThemeId = statementReactionDetails.definingThemeId
            )
        }

        userDefiningThemesService.addUserDefiningTheme(userDefiningTheme)
    }
}