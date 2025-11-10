package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.exception.NoDataFoundException
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

    private val userStatements = FakeStatementsData.userStatements

    private val userStatement = userStatements[0]
    private val statementReactionDetails = FakeStatementsData.statementReactionDetails

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getUserStatements_allData_success() {
        val expected = userStatements
        every { userStatementsRepository.findAllByUserIdAndDefiningThemeIds(userId, definingThemeIds) } returns expected

        val result = userStatementsService.getUserStatements(userId, definingThemeIds)

        assertEquals(expected, result)
    }

    @Test
    fun getUserStatements_emptyData_error() {
        every {
            userStatementsRepository.findAllByUserIdAndDefiningThemeIds(userId, definingThemeIds)
        } returns emptyList()

        assertThrows<NoDataFoundException> { userStatementsService.getUserStatements(userId, definingThemeIds) }
    }

    @Test
    fun processStatementReaction() {
        val expected = userStatement
        every { userStatementsRepository.save(statementReactionDetails.toUserStatement()) } returns expected
        every { kafkaProducer.updateUserCategory(statementReactionDetails.toUserCategoryUpdateDetails()) } just Runs

        val result = userStatementsService.processStatementReaction(statementReactionDetails)

        assertEquals(expected, result)
    }
}