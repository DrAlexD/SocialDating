package xelagurd.socialdating.server.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.additional.UserCategoryUpdateDetails

@Service
class StatementsKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, UserCategoryUpdateDetails>
) {

    fun updateUserCategory(userCategoryUpdateDetails: UserCategoryUpdateDetails) =
        kafkaTemplate.send(
            "update-user-category-on-statement-reaction",
            userCategoryUpdateDetails
        )
}