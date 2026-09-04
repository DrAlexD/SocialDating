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
import xelagurd.socialdating.server.model.details.CategoryUpdateDetails
import xelagurd.socialdating.server.model.details.UserCategoriesUpdateDetails
import xelagurd.socialdating.server.service.DefiningThemesKafkaProducer

@ExtendWith(MockKExtension::class)
class DefiningThemesKafkaProducerUnitTest {

    @MockK(relaxed = true)
    private lateinit var kafkaTemplate: KafkaTemplate<String, UserCategoriesUpdateDetails>

    @InjectMockKs
    private lateinit var definingThemesKafkaProducer: DefiningThemesKafkaProducer

    private val userCategoriesUpdateDetails = UserCategoriesUpdateDetails(
        userId = 1,
        categories = listOf(CategoryUpdateDetails(categoryId = 1))
    )

    @Test
    fun updateUserCategories_validData_sendsEventKeyedByUserIdToKafkaTopic() {
        definingThemesKafkaProducer.updateUserCategories(userCategoriesUpdateDetails)

        verify(exactly = 1) {
            kafkaTemplate.send(
                "update-user-categories-on-statement-reaction",
                "1",
                userCategoriesUpdateDetails
            )
        }
        confirmVerified(kafkaTemplate)
    }

    @Test
    fun updateUserCategories_sendFailure_isLoggedWithoutPropagation() {
        every { kafkaTemplate.send(any<String>(), any<String>(), any()) } returns
                CompletableFuture.failedFuture(RuntimeException("Kafka is unavailable"))

        definingThemesKafkaProducer.updateUserCategories(userCategoriesUpdateDetails)

        verify(exactly = 1) { kafkaTemplate.send(any<String>(), any<String>(), any()) }
        confirmVerified(kafkaTemplate)
    }
}
