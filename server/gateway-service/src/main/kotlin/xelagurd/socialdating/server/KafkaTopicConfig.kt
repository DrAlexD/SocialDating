package xelagurd.socialdating.server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.apache.kafka.clients.admin.NewTopic

@Configuration
class KafkaTopicConfig {

    @Bean
    fun createUpdateUserDefiningThemesOnStatementReactionTopic(): NewTopic =
        NewTopic("update-user-defining-themes-on-statement-reaction", 2, 1.toShort())

    @Bean
    fun createUpdateUserCategoriesOnStatementReactionTopic(): NewTopic =
        NewTopic("update-user-categories-on-statement-reaction", 2, 1.toShort())
}
