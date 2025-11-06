package xelagurd.socialdating.server.service

import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MIN
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.MaintainedListUpdateDetails
import xelagurd.socialdating.server.model.additional.UserCategoryUpdateDetails
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_NOT_MAINTAINED

@Profile("!test")
@Service
class CategoriesKafkaConsumer(
    private val userCategoriesService: UserCategoriesService,
    private val categoriesKafkaProducer: CategoriesKafkaProducer
) {

    @KafkaListener(topics = ["update-user-category-on-statement-reaction"], groupId = "categories-group")
    fun updateUserCategory(updateDetails: UserCategoryUpdateDetails) {
        val userCategory = userCategoriesService.getUserCategory(updateDetails.userId, updateDetails.categoryId)

        val updatedUserCategory = userCategory?.copy(
            interest = (userCategory.interest + CATEGORY_INTEREST_STEP).coerceIn(PERCENT_MIN, PERCENT_MAX)
        )
            ?: UserCategory(
                userId = updateDetails.userId,
                categoryId = updateDetails.categoryId
            )

        userCategoriesService.addUserCategory(updatedUserCategory)

        categoriesKafkaProducer.updateUserDefiningTheme(
            updateDetails.toUserDefiningThemeUpdateDetails()
        )
    }

    @KafkaListener(topics = ["update-maintained-list-on-statement-reaction"], groupId = "categories-group")
    fun updateMaintainedList(updateDetails: MaintainedListUpdateDetails) {
        val userCategory = userCategoriesService.getUserCategory(updateDetails.userId, updateDetails.categoryId)
            ?: return

        val updatedUserCategory = when (updateDetails.updateType) {
            INCREASE_NOT_MAINTAINED, DECREASE_NOT_MAINTAINED ->
                userCategory.copy(notMaintained = updateArray(userCategory.notMaintained, updateDetails))

            INCREASE_MAINTAINED, DECREASE_MAINTAINED ->
                userCategory.copy(maintained = updateArray(userCategory.maintained, updateDetails))
        }

        userCategoriesService.addUserCategory(updatedUserCategory)
    }

    private fun updateArray(
        array: Array<Long>?,
        updateDetails: MaintainedListUpdateDetails
    ): Array<Long> {
        val updatedArray = array?.toMutableList() ?: mutableListOf()

        val indexInCategory = updateDetails.numberInCategory - 1
        val arrayIndex = indexInCategory / Long.SIZE_BITS
        val bitIndex = indexInCategory % Long.SIZE_BITS
        val ensureSize = arrayIndex + 1

        while (updatedArray.size < ensureSize) {
            updatedArray.add(0L)
        }

        val value = updatedArray[arrayIndex]
        val bitMask = 1L shl bitIndex

        updatedArray[arrayIndex] = when (updateDetails.updateType) {
            INCREASE_NOT_MAINTAINED, INCREASE_MAINTAINED -> value or bitMask
            DECREASE_NOT_MAINTAINED, DECREASE_MAINTAINED -> value and bitMask.inv()
        }

        return updatedArray.toTypedArray()
    }
}