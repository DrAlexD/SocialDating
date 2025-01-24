package xelagurd.socialdating.service

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.dto.StatementReaction
import xelagurd.socialdating.dto.UserDefiningTheme

@Service
class KafkaStatementReactionConsumer(
    private val userDefiningThemesService: UserDefiningThemesService
) {

    @KafkaListener(topics = ["statement-reaction-to-user-category-topic"], groupId = "defining-themes-group")
    fun consumeData(statementReaction: StatementReaction) {
        var userDefiningTheme = userDefiningThemesService.getUserDefiningTheme(
            statementReaction.userOrUserCategoryId,
            statementReaction.definingThemeId
        )

        val diff = if (statementReaction.isSupportDefiningTheme) 5 else -5

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