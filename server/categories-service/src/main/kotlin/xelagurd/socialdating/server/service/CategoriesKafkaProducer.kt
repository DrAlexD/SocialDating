package xelagurd.socialdating.server.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.additional.StatementReactionDetails

@Service
class CategoriesKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, StatementReactionDetails>
) {

    fun sendStatementReaction(statementReactionDetails: StatementReactionDetails) {
        kafkaTemplate.send("statement-reaction-to-user-defining-theme-topic", statementReactionDetails)
    }
}