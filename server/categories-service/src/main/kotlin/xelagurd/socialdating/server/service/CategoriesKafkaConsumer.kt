package xelagurd.socialdating.server.service

import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import jakarta.validation.Valid
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MIN
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.details.MaintainedListUpdateDetails
import xelagurd.socialdating.server.model.details.UserCategoriesUpdateDetails
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.DECREASE_NOT_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_MAINTAINED
import xelagurd.socialdating.server.model.enums.MaintainedListUpdateType.INCREASE_NOT_MAINTAINED

@Profile("!test")
@Service
class CategoriesKafkaConsumer(
    private val userCategoriesService: UserCategoriesService
) {

    @Transactional
    @KafkaListener(topics = ["update-user-categories-on-statement-reaction"], groupId = "categories-group")
    fun updateUserCategories(@Payload @Valid updateDetails: UserCategoriesUpdateDetails) {
        updateDetails.categories.forEach { categoryUpdateDetails ->
            val userCategory = userCategoriesService
                .getUserCategory(updateDetails.userId, categoryUpdateDetails.categoryId)

            val updatedUserCategory = userCategory?.copy(
                interest = (userCategory.interest + CATEGORY_INTEREST_STEP).coerceIn(PERCENT_MIN, PERCENT_MAX)
            )
                ?: UserCategory(
                    userId = updateDetails.userId,
                    categoryId = categoryUpdateDetails.categoryId
                )

            categoryUpdateDetails.maintainedListUpdates.forEach {
                when (it.updateType) {
                    INCREASE_NOT_MAINTAINED, DECREASE_NOT_MAINTAINED ->
                        updatedUserCategory.notMaintained = updateList(updatedUserCategory.notMaintained, it)

                    INCREASE_MAINTAINED, DECREASE_MAINTAINED ->
                        updatedUserCategory.maintained = updateList(updatedUserCategory.maintained, it)
                }
            }

            userCategoriesService.addUserCategory(updatedUserCategory)
        }
    }

    private fun updateList(
        list: Array<Long>?,
        maintainedListUpdate: MaintainedListUpdateDetails
    ): Array<Long> {
        val updatedList = list?.toMutableList() ?: mutableListOf()

        val indexInCategory = maintainedListUpdate.numberInCategory - 1
        val listIndex = indexInCategory / Long.SIZE_BITS
        val bitIndex = indexInCategory % Long.SIZE_BITS
        val ensureSize = listIndex + 1

        while (updatedList.size < ensureSize) {
            updatedList.add(0L)
        }

        val value = updatedList[listIndex]
        val bitMask = 1L shl bitIndex

        updatedList[listIndex] = when (maintainedListUpdate.updateType) {
            INCREASE_NOT_MAINTAINED, INCREASE_MAINTAINED -> value or bitMask
            DECREASE_NOT_MAINTAINED, DECREASE_MAINTAINED -> value and bitMask.inv()
        }

        return updatedList.toTypedArray()
    }
}
