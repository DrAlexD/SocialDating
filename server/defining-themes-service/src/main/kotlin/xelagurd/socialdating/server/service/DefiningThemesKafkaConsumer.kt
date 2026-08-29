package xelagurd.socialdating.server.service

import kotlin.random.Random
import kotlin.random.nextInt
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_COEFFICIENT
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_HIGH_BORDER
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_INITIAL
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_LOW_BORDER
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MIN
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.common.CategoryUpdateDetails
import xelagurd.socialdating.server.model.common.MaintainedListUpdate
import xelagurd.socialdating.server.model.common.UserCategoriesUpdateDetails
import xelagurd.socialdating.server.model.common.UserDefiningThemesUpdateDetails
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.model.enums.StatementReactionType
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

    @Transactional
    @KafkaListener(topics = ["update-user-defining-themes-on-statement-reaction"], groupId = "defining-themes-group")
    fun updateUserDefiningThemes(updateDetails: UserDefiningThemesUpdateDetails) {
        val definingThemesById = definingThemesService
            .getDefiningThemes(definingThemeIds = updateDetails.definingThemes.map { it.definingThemeId })
            .associateBy { it.id!! }

        val maintainedListUpdatesByCategoryId = linkedMapOf<Int, MutableList<MaintainedListUpdate>>()

        updateDetails.definingThemes.forEach { definingThemeReaction ->
            val userDefiningTheme = userDefiningThemesService
                .getUserDefiningTheme(updateDetails.userId, definingThemeReaction.definingThemeId)

            val diff = calculateDiff(updateDetails.reactionType, definingThemeReaction.isSupportDefiningTheme)

            val updatedUserDefiningTheme = userDefiningTheme?.copy(
                value = (userDefiningTheme.value + diff).coerceIn(PERCENT_MIN, PERCENT_MAX),
                interest = (userDefiningTheme.interest + DEFINING_THEME_INTEREST_STEP).coerceIn(PERCENT_MIN, PERCENT_MAX)
            )
                ?: UserDefiningTheme(
                    value = DEFINING_THEME_VALUE_INITIAL + diff,
                    userId = updateDetails.userId,
                    definingThemeId = definingThemeReaction.definingThemeId
                )

            userDefiningThemesService.addUserDefiningTheme(updatedUserDefiningTheme)

            val definingTheme = definingThemesById[definingThemeReaction.definingThemeId] ?: return@forEach
            val maintainedListUpdates = maintainedListUpdatesByCategoryId
                .getOrPut(definingTheme.categoryId) { mutableListOf() }

            userDefiningTheme?.value?.let { value ->
                determineUpdateType(value, diff)?.let {
                    maintainedListUpdates += MaintainedListUpdate(
                        updateType = it,
                        numberInCategory = definingTheme.numberInCategory
                    )
                }
            }
        }

        if (maintainedListUpdatesByCategoryId.isEmpty()) return

        definingThemesKafkaProducer.updateUserCategories(
            UserCategoriesUpdateDetails(
                userId = updateDetails.userId,
                categories = maintainedListUpdatesByCategoryId.map { (categoryId, maintainedListUpdates) ->
                    CategoryUpdateDetails(
                        categoryId = categoryId,
                        maintainedListUpdates = maintainedListUpdates
                    )
                }
            )
        )
    }

    private fun calculateDiff(
        reactionType: StatementReactionType,
        isSupportDefiningTheme: Boolean
    ): Int {
        val diff = when (reactionType) {
            FULL_NO_MAINTAIN -> -DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT
            PART_NO_MAINTAIN -> -DEFINING_THEME_VALUE_STEP
            NOT_SURE -> Random.nextInt(-1..1)
            PART_MAINTAIN -> DEFINING_THEME_VALUE_STEP
            FULL_MAINTAIN -> DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT
        }

        return if (isSupportDefiningTheme) diff else -diff
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
