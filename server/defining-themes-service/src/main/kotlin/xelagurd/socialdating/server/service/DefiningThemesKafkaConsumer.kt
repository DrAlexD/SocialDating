package xelagurd.socialdating.server.service

import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_COEFFICIENT
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_INITIAL
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_STEP
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.additional.UserDefiningThemeUpdateDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType

@Profile("!test")
@Service
class DefiningThemesKafkaConsumer(
    private val userDefiningThemesService: UserDefiningThemesService
) {

    @KafkaListener(topics = ["update-user-defining-theme-on-statement-reaction"], groupId = "defining-themes-group")
    fun updateUserDefiningTheme(userDefiningThemeUpdateDetails: UserDefiningThemeUpdateDetails) {
        var userDefiningTheme = userDefiningThemesService.getUserDefiningTheme(
            userDefiningThemeUpdateDetails.userId,
            userDefiningThemeUpdateDetails.definingThemeId
        )

        var diff = when (userDefiningThemeUpdateDetails.reactionType) {
            StatementReactionType.FULL_NO_MAINTAIN -> -DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT
            StatementReactionType.PART_NO_MAINTAIN -> -DEFINING_THEME_VALUE_STEP
            StatementReactionType.NOT_SURE -> 0
            StatementReactionType.PART_MAINTAIN -> DEFINING_THEME_VALUE_STEP
            StatementReactionType.FULL_MAINTAIN -> DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT
        }

        if (!userDefiningThemeUpdateDetails.isSupportDefiningTheme) {
            diff = -diff
        }

        userDefiningTheme = userDefiningTheme?.copy(
            value = userDefiningTheme.value + diff,
            interest = userDefiningTheme.interest + DEFINING_THEME_INTEREST_STEP
        )
            ?: UserDefiningTheme(
                value = DEFINING_THEME_VALUE_INITIAL + diff,
                userId = userDefiningThemeUpdateDetails.userId,
                definingThemeId = userDefiningThemeUpdateDetails.definingThemeId
            )

        userDefiningThemesService.addUserDefiningTheme(userDefiningTheme)
    }
}