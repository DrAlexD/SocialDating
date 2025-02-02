package xelagurd.socialdating.server.service

import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.StatementReactionDetails

@Profile("!test")
@Service
class CategoriesKafkaConsumer(
    private val userCategoriesService: UserCategoriesService,
    private val kafkaProducer: CategoriesKafkaProducer
) {

    @KafkaListener(topics = ["statement-reaction-to-user-category-topic"], groupId = "categories-group")
    fun consumeStatementReaction(statementReactionDetails: StatementReactionDetails) {
        var userCategory = userCategoriesService.getUserCategory(
            statementReactionDetails.userOrUserCategoryId,
            statementReactionDetails.categoryId
        )

        if (userCategory != null) {
            userCategory.interest = userCategory.interest + 5
        } else {
            userCategory = UserCategory(
                interest = 5,
                userId = statementReactionDetails.userOrUserCategoryId,
                categoryId = statementReactionDetails.categoryId
            )
        }

        val addedUserCategory = userCategoriesService.addUserCategory(userCategory)

        kafkaProducer.sendStatementReaction(
            statementReactionDetails.copy(userOrUserCategoryId = addedUserCategory.id!!)
        )
    }
}