package xelagurd.socialdating.server.service

import kotlin.random.Random
import kotlin.random.nextInt
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_COEFFICIENT
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_HIGH_BORDER
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_INITIAL
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_LOW_BORDER
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MIN
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.common.MaintainedListUpdateDetails
import xelagurd.socialdating.server.model.common.UserDefiningThemeUpdateDetails
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_NO_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.NOT_SURE
import xelagurd.socialdating.server.model.enums.StatementReactionType.PART_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.PART_NO_MAINTAIN

@Profile("!test")
@Service
class DefiningThemesKafkaConsumer(
    private val userDefiningThemesService: UserDefiningThemesService,
    private val definingThemesService: DefiningThemesService,
    private val definingThemesKafkaProducer: DefiningThemesKafkaProducer
) {

    @KafkaListener(topics = ["update-user-defining-theme-on-statement-reaction"], groupId = "defining-themes-group")
    fun updateUserDefiningTheme(updateDetails: UserDefiningThemeUpdateDetails) {
        val userDefiningTheme = userDefiningThemesService
            .getUserDefiningTheme(updateDetails.userId, updateDetails.definingThemeId)

        val diff = calculateDiff(updateDetails)

        val updatedUserDefiningTheme = userDefiningTheme?.copy(
            value = (userDefiningTheme.value + diff).coerceIn(PERCENT_MIN, PERCENT_MAX),
            interest = (userDefiningTheme.interest + DEFINING_THEME_INTEREST_STEP).coerceIn(PERCENT_MIN, PERCENT_MAX)
        )
            ?: UserDefiningTheme(
                value = DEFINING_THEME_VALUE_INITIAL + diff,
                userId = updateDetails.userId,
                definingThemeId = updateDetails.definingThemeId
            )

        userDefiningThemesService.addUserDefiningTheme(updatedUserDefiningTheme)

        updateMaintainedListIfNeeded(userDefiningTheme, updateDetails, diff)
    }

    private fun updateMaintainedListIfNeeded(
        userDefiningTheme: UserDefiningTheme?,
        updateDetails: UserDefiningThemeUpdateDetails,
        diff: Int
    ) {
        userDefiningTheme?.value?.let {
            determineUpdateType(it, diff)?.let {
                val definingTheme = definingThemesService.getDefiningTheme(updateDetails.definingThemeId)
                    ?: return

                definingThemesKafkaProducer.updateMaintainedList(
                    MaintainedListUpdateDetails(
                        userId = updateDetails.userId,
                        categoryId = definingTheme.categoryId,
                        updateType = it,
                        numberInCategory = definingTheme.numberInCategory
                    )
                )
            }
        }
    }

    private fun calculateDiff(updateDetails: UserDefiningThemeUpdateDetails): Int {
        val diff = when (updateDetails.reactionType) {
            FULL_NO_MAINTAIN -> -DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT
            PART_NO_MAINTAIN -> -DEFINING_THEME_VALUE_STEP
            NOT_SURE -> Random.nextInt(-1..1)
            PART_MAINTAIN -> DEFINING_THEME_VALUE_STEP
            FULL_MAINTAIN -> DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT
        }

        return if (updateDetails.isSupportDefiningTheme) diff else -diff
    }

    private fun determineUpdateType(value: Int, diff: Int) =
        when {
            value > DEFINING_THEME_VALUE_LOW_BORDER && value + diff <= DEFINING_THEME_VALUE_LOW_BORDER
                -> INCREASE_NOT_MAINTAINED

            value <= DEFINING_THEME_VALUE_LOW_BORDER && value + diff > DEFINING_THEME_VALUE_LOW_BORDER
                -> DECREASE_NOT_MAINTAINED

            value >= DEFINING_THEME_VALUE_HIGH_BORDER && value + diff < DEFINING_THEME_VALUE_HIGH_BORDER
                -> DECREASE_MAINTAINED

            value < DEFINING_THEME_VALUE_HIGH_BORDER && value + diff >= DEFINING_THEME_VALUE_HIGH_BORDER
                -> INCREASE_MAINTAINED

            else -> null
        }
}