package xelagurd.socialdating.server

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.apache.kafka.clients.admin.NewTopic

@Configuration
class KafkaTopicConfig {

    @Bean
    fun createUpdateUserCategoryOnStatementReactionTopic(): NewTopic =
        NewTopic("update-user-category-on-statement-reaction", 2, 1.toShort())

    @Bean
    fun createUpdateUserDefiningThemeOnStatementReactionTopic(): NewTopic =
        NewTopic("update-user-defining-theme-on-statement-reaction", 2, 1.toShort())

    @Bean
    fun createUpdateMaintainedListOnStatementReactionTopic(): NewTopic =
        NewTopic("update-maintained-list-on-statement-reaction", 2, 1.toShort())
}
