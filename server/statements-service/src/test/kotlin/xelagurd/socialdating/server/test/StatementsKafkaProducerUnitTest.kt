package xelagurd.socialdating.server.test

import java.util.concurrent.CompletableFuture
import org.springframework.kafka.core.KafkaTemplate
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.model.details.DefiningThemeReactionDetails
import xelagurd.socialdating.server.model.details.UserDefiningThemesUpdateDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.service.StatementsKafkaProducer

@ExtendWith(MockKExtension::class)
class StatementsKafkaProducerUnitTest {

    @MockK(relaxed = true)
    private lateinit var kafkaTemplate: KafkaTemplate<String, UserDefiningThemesUpdateDetails>

    @InjectMockKs
    private lateinit var statementsKafkaProducer: StatementsKafkaProducer

    private val userDefiningThemesUpdateDetails = UserDefiningThemesUpdateDetails(
        userId = 1,
        reactionType = FULL_MAINTAIN,
        definingThemes = listOf(DefiningThemeReactionDetails(1, true))
    )

    @Test
    fun updateUserDefiningThemes_validData_sendsEventKeyedByUserIdToKafkaTopic() {
        statementsKafkaProducer.updateUserDefiningThemes(userDefiningThemesUpdateDetails)

        verify(exactly = 1) {
            kafkaTemplate.send(
                "update-user-defining-themes-on-statement-reaction",
                "1",
                userDefiningThemesUpdateDetails
            )
        }
        confirmVerified(kafkaTemplate)
    }

    @Test
    fun updateUserDefiningThemes_sendFailure_isLoggedWithoutPropagation() {
        every { kafkaTemplate.send(any<String>(), any<String>(), any()) } returns
                CompletableFuture.failedFuture(RuntimeException("Kafka is unavailable"))

        statementsKafkaProducer.updateUserDefiningThemes(userDefiningThemesUpdateDetails)

        verify(exactly = 1) { kafkaTemplate.send(any<String>(), any<String>(), any()) }
        confirmVerified(kafkaTemplate)
    }
}
