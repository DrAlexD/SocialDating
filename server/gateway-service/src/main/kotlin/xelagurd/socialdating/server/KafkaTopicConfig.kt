package xelagurd.socialdating.server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.apache.kafka.clients.admin.NewTopic

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
