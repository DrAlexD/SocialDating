package xelagurd.socialdating.service

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
import org.springframework.data.repository.findByIdOrNull
import xelagurd.socialdating.dto.Statement
import xelagurd.socialdating.dto.StatementDetails
import xelagurd.socialdating.dto.StatementReactionDetails
import xelagurd.socialdating.dto.StatementReactionType
import xelagurd.socialdating.repository.StatementsRepository

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
        userId = userId,
        categoryId = 1,
        reactionType = StatementReactionType.FULL_MAINTAIN
    )

    private val statementReaction = statementReactionDetails.toStatementReaction(
        definingThemeId = 1,
        isSupportDefiningTheme = true
    )

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getStatementsByDefiningThemeIds() {
        val expected = statements.filter { it.definingThemeId in definingThemeIds }
        every { statementsRepository.findAllByDefiningThemeIdIn(definingThemeIds) } returns expected

        val result = statementsService.getStatements(definingThemeIds)

        assertEquals(expected, result)
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
        every { kafkaProducer.sendStatementReaction(statementReactionDetails.toStatementReaction(1, true)) } just Runs

        statementsService.addStatementReaction(statementId, statementReactionDetails)

        verify { kafkaProducer.sendStatementReaction(statementReaction) }
    }
}