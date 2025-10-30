package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType
import xelagurd.socialdating.server.service.CategoriesKafkaConsumer
import xelagurd.socialdating.server.service.CategoriesKafkaProducer
import xelagurd.socialdating.server.service.UserCategoriesService

class CategoriesKafkaConsumerUnitTest {

    @MockK
    private lateinit var userCategoriesService: UserCategoriesService

    @MockK
    private lateinit var kafkaProducer: CategoriesKafkaProducer

    @InjectMockKs
    private lateinit var categoriesKafkaConsumer: CategoriesKafkaConsumer

    val userId = 1
    val categoryId = 1

    private val statementReactionDetails = StatementReactionDetails(
        userOrUserCategoryId = userId,
        categoryId = categoryId,
        definingThemeId = 1,
        statementId = 1,
        reactionType = StatementReactionType.FULL_MAINTAIN,
        isSupportDefiningTheme = true
    )

    private val userCategory = UserCategory(id = 1, interest = 10, userId = userId, categoryId = categoryId)
    private val updatedUserCategory = UserCategory(id = 1, interest = 15, userId = userId, categoryId = categoryId)
    private val newUserCategory = UserCategory(
        interest = 5,
        userId = statementReactionDetails.userOrUserCategoryId,
        categoryId = statementReactionDetails.categoryId
    )
    private val newAddedUserCategory = UserCategory(
        id = 1,
        interest = 5,
        userId = statementReactionDetails.userOrUserCategoryId,
        categoryId = statementReactionDetails.categoryId
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun consumeStatementReactionWithExistUserCategory() {
        every {
            userCategoriesService.getUserCategory(
                statementReactionDetails.userOrUserCategoryId,
                statementReactionDetails.categoryId
            )
        } returns userCategory

        every { userCategoriesService.addUserCategory(userCategory) } returns updatedUserCategory

        every {
            kafkaProducer.sendStatementReaction(
                statementReactionDetails.copy(userOrUserCategoryId = updatedUserCategory.id)
            )
        } just Runs

        categoriesKafkaConsumer.consumeStatementReaction(statementReactionDetails)
    }

    @Test
    fun consumeStatementReactionWithNotExistUserCategory() {
        every {
            userCategoriesService.getUserCategory(
                statementReactionDetails.userOrUserCategoryId,
                statementReactionDetails.categoryId
            )
        } returns null

        every { userCategoriesService.addUserCategory(newUserCategory) } returns newAddedUserCategory

        every {
            kafkaProducer.sendStatementReaction(
                statementReactionDetails.copy(userOrUserCategoryId = newAddedUserCategory.id)
            )
        } just Runs

        categoriesKafkaConsumer.consumeStatementReaction(statementReactionDetails)
    }
}