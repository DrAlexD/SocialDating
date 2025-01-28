package xelagurd.socialdating.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import xelagurd.socialdating.dto.StatementReaction

@Service
class CategoriesKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, StatementReaction>
) {

    fun sendStatementReaction(statementReaction: StatementReaction) {
        kafkaTemplate.send("statement-reaction-to-user-defining-theme-topic", statementReaction)
    }
}