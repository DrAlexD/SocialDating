package xelagurd.socialdating.server.test

import org.springframework.data.repository.findByIdOrNull
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
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType
import xelagurd.socialdating.server.repository.StatementsRepository
import xelagurd.socialdating.server.service.StatementsKafkaProducer
import xelagurd.socialdating.server.service.StatementsService

class StatementsServiceUnitTest {

    @MockK
    private lateinit var statementsRepository: StatementsRepository

    @MockK
    private lateinit var kafkaProducer: StatementsKafkaProducer

    @InjectMockKs
    private lateinit var statementsService: StatementsService

    private val userId = 1
    private val definingThemeIds = listOf(1, 3)

    private val statements = listOf(
        Statement(
            id = 1,
            text = "RemoteStatement1",
            isSupportDefiningTheme = true,
            definingThemeId = 1,
            creatorUserId = userId
        ),
        Statement(
            id = 2,
            text = "RemoteStatement2",
            isSupportDefiningTheme = true,
            definingThemeId = 2,
            creatorUserId = userId
        ),
        Statement(
            id = 3,
            text = "RemoteStatement3",
            isSupportDefiningTheme = true,
            definingThemeId = 3,
            creatorUserId = userId
        )
    )

    private val statementDetails = StatementDetails(
        text = "RemoteStatement1",
        isSupportDefiningTheme = true,
        definingThemeId = 1,
        creatorUserId = userId
    )

    private val statementId = 1

    private val statementReactionDetails = StatementReactionDetails(
        userOrUserCategoryId = userId,
        categoryId = 1,
        definingThemeId = 1,
        reactionType = StatementReactionType.FULL_MAINTAIN,
        isSupportDefiningTheme = true
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getStatementsByDefiningThemeIds_allData_success() {
        val expected = statements.filter { it.definingThemeId in definingThemeIds }
        every { statementsRepository.findAllByDefiningThemeIdIn(definingThemeIds) } returns expected

        val result = statementsService.getStatements(definingThemeIds)

        assertEquals(expected, result)
    }

    @Test
    fun getStatementsByDefiningThemeIds_emptyData_error() {
        every { statementsRepository.findAllByDefiningThemeIdIn(definingThemeIds) } returns emptyList()

        assertThrows<NoDataFoundException> { statementsService.getStatements(definingThemeIds) }
    }

    @Test
    fun addStatement() {
        val expected = statements[0]
        every { statementsRepository.save(statementDetails.toStatement()) } returns expected

        val result = statementsService.addStatement(statementDetails)

        assertEquals(expected, result)
    }

    @Test
    fun addStatementReaction() {
        val expected = statements.filter { it.id == statementId }

        assertEquals(expected.size, 1)

        every { statementsRepository.findByIdOrNull(statementId) } returns expected[0]
        every { kafkaProducer.sendStatementReaction(statementReactionDetails) } just Runs

        statementsService.addStatementReaction(statementId, statementReactionDetails)

        verify { kafkaProducer.sendStatementReaction(statementReactionDetails) }
    }
}