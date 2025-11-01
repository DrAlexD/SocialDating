package xelagurd.socialdating.server.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.additional.UserDefiningThemeUpdateDetails

@Service
class CategoriesKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, UserDefiningThemeUpdateDetails>
) {

    fun sendStatementReaction(userDefiningThemeUpdateDetails: UserDefiningThemeUpdateDetails) {
        kafkaTemplate.send("statement-reaction-to-user-defining-theme-topic", userDefiningThemeUpdateDetails)
    }
}