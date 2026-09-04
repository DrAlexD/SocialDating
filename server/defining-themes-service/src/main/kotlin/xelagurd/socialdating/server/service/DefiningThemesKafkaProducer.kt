package xelagurd.socialdating.server.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import io.github.oshai.kotlinlogging.KotlinLogging
import xelagurd.socialdating.server.model.details.UserCategoriesUpdateDetails

@Service
class DefiningThemesKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, UserCategoriesUpdateDetails>
) {
    val logger = KotlinLogging.logger { }

    fun updateUserCategories(userCategoriesUpdateDetails: UserCategoriesUpdateDetails) {
        kafkaTemplate.send(
            "update-user-categories-on-statement-reaction",
            userCategoriesUpdateDetails.userId.toString(),
            userCategoriesUpdateDetails
        ).whenComplete { _, exception ->
            if (exception != null) {
                logger.error(exception) {
                    "Failed to send user categories update of user ${userCategoriesUpdateDetails.userId}"
                }
            }
        }
    }
}
