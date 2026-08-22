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
import xelagurd.socialdating.server.model.common.UserDefiningThemeUpdateDetails
import xelagurd.socialdating.server.service.CategoriesKafkaProducer

@ExtendWith(MockKExtension::class)
class CategoriesKafkaProducerUnitTest {

    @MockK(relaxed = true)
    private lateinit var kafkaTemplate: KafkaTemplate<String, UserDefiningThemeUpdateDetails>

    @InjectMockKs
    private lateinit var categoriesKafkaProducer: CategoriesKafkaProducer

    private val userDefiningThemeUpdateDetails = mockk<UserDefiningThemeUpdateDetails>(relaxed = true)

    @Test
    fun updateUserDefiningTheme_validData_sendsEventToKafkaTopic() {
        categoriesKafkaProducer.updateUserDefiningTheme(userDefiningThemeUpdateDetails)

        verify(exactly = 1) {
            kafkaTemplate.send(
                "update-user-defining-theme-on-statement-reaction",
                userDefiningThemeUpdateDetails
            )
        }
        confirmVerified(kafkaTemplate)
    }
}
