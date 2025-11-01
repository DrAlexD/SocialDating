package xelagurd.socialdating.server.service

import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_STEP
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.UserCategoryUpdateDetails

@Profile("!test")
@Service
class CategoriesKafkaConsumer(
    private val userCategoriesService: UserCategoriesService,
    private val kafkaProducer: CategoriesKafkaProducer
) {

    @KafkaListener(topics = ["update-user-category-on-statement-reaction"], groupId = "categories-group")
    fun updateUserCategory(userCategoryUpdateDetails: UserCategoryUpdateDetails) {
        var userCategory = userCategoriesService.getUserCategory(
            userCategoryUpdateDetails.userId,
            userCategoryUpdateDetails.categoryId
        )

        if (userCategory != null) {
            userCategory.interest += CATEGORY_INTEREST_STEP
        } else {
            userCategory = UserCategory(
                userId = userCategoryUpdateDetails.userId,
                categoryId = userCategoryUpdateDetails.categoryId
            )
        }

        userCategoriesService.addUserCategory(userCategory)

        kafkaProducer.updateUserDefiningTheme(
            userCategoryUpdateDetails.toUserDefiningThemeUpdateDetails()
        )
    }
}