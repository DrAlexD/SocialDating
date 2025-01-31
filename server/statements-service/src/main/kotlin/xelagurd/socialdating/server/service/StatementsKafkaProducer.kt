package xelagurd.socialdating.server.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import xelagurd.socialdating.common.dto.StatementReaction

@Service
class StatementsKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, StatementReaction>
) {

    fun sendStatementReaction(statementReaction: StatementReaction) {
        kafkaTemplate.send("statement-reaction-to-user-category-topic", statementReaction)
    }
}