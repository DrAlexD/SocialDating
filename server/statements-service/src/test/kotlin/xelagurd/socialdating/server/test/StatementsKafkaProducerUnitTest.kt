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
import xelagurd.socialdating.server.model.common.UserCategoryUpdateDetails
import xelagurd.socialdating.server.service.StatementsKafkaProducer

@ExtendWith(MockKExtension::class)
class StatementsKafkaProducerUnitTest {

    @MockK(relaxed = true)
    private lateinit var kafkaTemplate: KafkaTemplate<String, UserCategoryUpdateDetails>

    @InjectMockKs
    private lateinit var statementsKafkaProducer: StatementsKafkaProducer

    private val userCategoryUpdateDetails = mockk<UserCategoryUpdateDetails>(relaxed = true)

    @Test
    fun updateUserCategory_validData_sendsEventToKafkaTopic() {
        statementsKafkaProducer.updateUserCategory(userCategoryUpdateDetails)

        verify(exactly = 1) {
            kafkaTemplate.send("update-user-category-on-statement-reaction", userCategoryUpdateDetails)
        }
        confirmVerified(kafkaTemplate)
    }
}
