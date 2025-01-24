package xelagurd.socialdating

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaTopicConfig {

    @Bean
    fun createStatementReactionToUserCategoryTopic(): NewTopic {
        return NewTopic("statement-reaction-to-user-category-topic", 2, 1.toShort())
    }

    @Bean
    fun createStatementReactionToUserDefiningThemeTopic(): NewTopic {
        return NewTopic("statement-reaction-to-user-defining-theme-topic", 2, 1.toShort())
    }
}
