package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.UserStatementDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.repository.UserStatementsRepository
import xelagurd.socialdating.server.service.StatementsKafkaProducer
import xelagurd.socialdating.server.service.UserStatementsService

class UserStatementsServiceUnitTest {

    @MockK
    private lateinit var userStatementsRepository: UserStatementsRepository

    @MockK
    private lateinit var kafkaProducer: StatementsKafkaProducer

    @InjectMockKs
    private lateinit var userStatementsService: UserStatementsService

    private val userId = 1
    private val definingThemeIds = listOf(1, 3)

    private val userStatements = listOf(
        UserStatement(id = 1, reactionType = FULL_MAINTAIN, userId = 1, statementId = 1),
        UserStatement(id = 2, reactionType = FULL_MAINTAIN, userId = 1, statementId = 2),
        UserStatement(id = 3, reactionType = FULL_MAINTAIN, userId = 2, statementId = 3)
    )

    private val userStatementDetails = UserStatementDetails(reactionType = FULL_MAINTAIN, userId = userId, statementId = 1)

    private val statementReactionDetails = StatementReactionDetails(
        userId = userId,
        statementId = 1,
        categoryId = 1,
        definingThemeId = 1,
        reactionType = FULL_MAINTAIN,
        isSupportDefiningTheme = true
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUserStatements_allData_success() {
        val expected = userStatements.filter { it.userId == userId }
        every { userStatementsRepository.findAllByUserIdAndDefiningThemeIds(userId, definingThemeIds) } returns expected

        val result = userStatementsService.getUserStatements(userId, definingThemeIds)

        assertEquals(expected, result)
    }

    @Test
    fun getUserStatements_emptyData_error() {
        every { userStatementsRepository.findAllByUserIdAndDefiningThemeIds(userId, definingThemeIds) } returns emptyList()

        assertThrows<NoDataFoundException> { userStatementsService.getUserStatements(userId, definingThemeIds) }
    }

    @Test
    fun addUserStatement() {
        val expected = userStatements[0]
        every { userStatementsRepository.save(userStatementDetails.toUserStatement()) } returns expected

        val result = userStatementsService.addUserStatement(userStatementDetails)

        assertEquals(expected, result)
    }

    @Test
    fun handleStatementReaction() {
        val expected = userStatements[0]
        every { userStatementsRepository.save(userStatementDetails.toUserStatement()) } returns expected
        every { kafkaProducer.updateUserCategory(statementReactionDetails.toUserCategoryUpdateDetails()) } just Runs

        userStatementsService.handleStatementReaction(statementReactionDetails)

        verify { userStatementsRepository.save(userStatementDetails.toUserStatement()) }
        verify { kafkaProducer.updateUserCategory(statementReactionDetails.toUserCategoryUpdateDetails()) }
    }
}