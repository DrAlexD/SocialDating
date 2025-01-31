package xelagurd.socialdating.server.service

import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import xelagurd.socialdating.common.dto.StatementReaction
import xelagurd.socialdating.server.model.UserCategory

@Profile("!test")
@Service
class CategoriesKafkaConsumer(
    private val userCategoriesService: UserCategoriesService,
    private val kafkaProducer: CategoriesKafkaProducer
) {

    @KafkaListener(topics = ["statement-reaction-to-user-category-topic"], groupId = "categories-group")
    fun consumeStatementReaction(statementReaction: StatementReaction) {
        var userCategory = userCategoriesService.getUserCategory(
            statementReaction.userOrUserCategoryId,
            statementReaction.categoryId
        )

        if (userCategory != null) {
            userCategory.interest = userCategory.interest!! + 5
        } else {
            userCategory = UserCategory(
                interest = 5,
                userId = statementReaction.userOrUserCategoryId,
                categoryId = statementReaction.categoryId
            )
        }

        val addedUserCategory = userCategoriesService.addUserCategory(userCategory)

        kafkaProducer.sendStatementReaction(
            statementReaction.copy(userOrUserCategoryId = addedUserCategory.id!!)
        )
    }
}