package xelagurd.socialdating.server.service

import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MAX
import xelagurd.socialdating.server.model.DefaultDataProperties.PERCENT_MIN
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.UserCategoryUpdateDetails

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
}