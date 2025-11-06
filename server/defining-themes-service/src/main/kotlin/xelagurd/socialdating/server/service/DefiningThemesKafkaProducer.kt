package xelagurd.socialdating.server.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import xelagurd.socialdating.server.model.additional.MaintainedListUpdateDetails

@Service
class DefiningThemesKafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, MaintainedListUpdateDetails>
) {

    fun updateMaintainedList(maintainedListUpdateDetails: MaintainedListUpdateDetails) {
        kafkaTemplate.send(
            "update-maintained-list-on-statement-reaction",
            maintainedListUpdateDetails
        )
    }
}