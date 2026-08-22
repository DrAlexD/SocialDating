package xelagurd.socialdating.server.test

import org.springframework.kafka.core.KafkaTemplate
import io.mockk.confirmVerified
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.model.common.MaintainedListUpdateDetails
import xelagurd.socialdating.server.service.DefiningThemesKafkaProducer

@ExtendWith(MockKExtension::class)
class DefiningThemesKafkaProducerUnitTest {

    @MockK(relaxed = true)
    private lateinit var kafkaTemplate: KafkaTemplate<String, MaintainedListUpdateDetails>

    @InjectMockKs
    private lateinit var definingThemesKafkaProducer: DefiningThemesKafkaProducer

    private val maintainedListUpdateDetails = mockk<MaintainedListUpdateDetails>(relaxed = true)

    @Test
    fun updateMaintainedList_validData_sendsEventToKafkaTopic() {
        definingThemesKafkaProducer.updateMaintainedList(maintainedListUpdateDetails)

        verify(exactly = 1) {
            kafkaTemplate.send("update-maintained-list-on-statement-reaction", maintainedListUpdateDetails)
        }
        confirmVerified(kafkaTemplate)
    }
}
