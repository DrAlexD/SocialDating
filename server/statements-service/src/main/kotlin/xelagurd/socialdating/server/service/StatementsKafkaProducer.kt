package xelagurd.socialdating.server.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import io.github.oshai.kotlinlogging.KotlinLogging
import xelagurd.socialdating.server.model.details.UserDefiningThemesUpdateDetails

@Service
class StatementsKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, UserDefiningThemesUpdateDetails>
) {
    val logger = KotlinLogging.logger { }

    fun updateUserDefiningThemes(userDefiningThemesUpdateDetails: UserDefiningThemesUpdateDetails) {
        kafkaTemplate.send(
            "update-user-defining-themes-on-statement-reaction",
            userDefiningThemesUpdateDetails.userId.toString(),
            userDefiningThemesUpdateDetails
        ).whenComplete { _, exception ->
            if (exception != null) {
                logger.error(exception) {
                    "Failed to send defining themes update of user ${userDefiningThemesUpdateDetails.userId}"
                }
            }
        }
    }
}
