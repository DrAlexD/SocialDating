package xelagurd.socialdating.server.test

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.repository.StatementsRepository
import xelagurd.socialdating.server.service.StatementsService

class StatementsServiceUnitTest {

    @MockK
    private lateinit var statementsRepository: StatementsRepository

    @InjectMockKs
    private lateinit var statementsService: StatementsService

    private val userId = 1
    private val definingThemeIds = listOf(1, 3)

    private val statements = FakeStatementsData.statements

    private val statementDetails = FakeStatementsData.statementsDetails[0]
    private val statement = statements[0]

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getStatements_allData_success() {
        val expected = statements
        every { statementsRepository.findUnreactedStatements(userId, definingThemeIds) } returns expected

        val result = statementsService.getStatements(userId, definingThemeIds)

        assertEquals(expected, result)
    }

    @Test
    fun getStatements_emptyData_error() {
        every { statementsRepository.findUnreactedStatements(userId, definingThemeIds) } returns emptyList()

        assertThrows<NoDataFoundException> { statementsService.getStatements(userId, definingThemeIds) }
    }

    @Test
    fun addStatement() {
        val expected = statement
        every { statementsRepository.save(statementDetails.toStatement()) } returns expected

        val result = statementsService.addStatement(statementDetails)

        assertEquals(expected, result)
    }
}