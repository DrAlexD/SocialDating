package xelagurd.socialdating.service

import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.dto.StatementReaction
import xelagurd.socialdating.dto.StatementReactionType
import xelagurd.socialdating.dto.UserDefiningTheme

@Profile("!test")
@Service
class DefiningThemesKafkaConsumer(
    private val userDefiningThemesService: UserDefiningThemesService
) {

    @KafkaListener(topics = ["statement-reaction-to-user-defining-theme-topic"], groupId = "defining-themes-group")
    fun consumeStatementReaction(statementReaction: StatementReaction) {
        var userDefiningTheme = userDefiningThemesService.getUserDefiningTheme(
            statementReaction.userOrUserCategoryId,
            statementReaction.definingThemeId
        )

        var diff = when (statementReaction.reactionType) {
            StatementReactionType.FULL_NO_MAINTAIN -> -10
            StatementReactionType.PART_NO_MAINTAIN -> -5
            StatementReactionType.NOT_SURE -> 0
            StatementReactionType.PART_MAINTAIN -> 5
            StatementReactionType.FULL_MAINTAIN -> 10
        }

        if (!statementReaction.isSupportDefiningTheme) {
            diff = -diff
        }

        if (userDefiningTheme != null) {
            userDefiningTheme.value = userDefiningTheme.value!! + diff

            userDefiningTheme.interest = userDefiningTheme.interest!! + 5
        } else {
            userDefiningTheme = UserDefiningTheme(
                value = 50 + diff,
                interest = 5,
                userCategoryId = statementReaction.userOrUserCategoryId,
                definingThemeId = statementReaction.definingThemeId
            )
        }

        userDefiningThemesService.addUserDefiningTheme(userDefiningTheme)
    }
}