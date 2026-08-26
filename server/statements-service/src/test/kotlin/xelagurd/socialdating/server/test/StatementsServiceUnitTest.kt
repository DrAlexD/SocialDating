package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.repository.StatementsRepository
import xelagurd.socialdating.server.service.StatementsService
import xelagurd.socialdating.server.utils.TestUtils.nextIntList

@ExtendWith(MockKExtension::class)
class StatementsServiceUnitTest {

    @MockK
    private lateinit var statementsRepository: StatementsRepository

    @InjectMockKs
    private lateinit var statementsService: StatementsService

    private val currentUserId = Random.nextInt(1, Int.MAX_VALUE)
    private val definingThemeIds = Random.nextIntList()
    private val statements = FakeStatementsData.statements
    private val statementDetails = FakeStatementsData.statementsDetails[0]
    private val statementSlot = slot<Statement>()

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    private fun setAuthenticatedUser(userId: Int) {
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns userId
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    fun getStatements_authorized_returnsUnreactedStatements() {
        setAuthenticatedUser(currentUserId)
        every { statementsRepository.findUnreactedStatements(any(), any()) } returns statements

        val result = statementsService.getStatements(currentUserId, definingThemeIds)

        assertEquals(statements, result)

        verify(exactly = 1) { statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds) }
        confirmVerified(statementsRepository)
    }

    @Test
    fun getStatements_anotherUser_throwsAccessDenied() {
        setAuthenticatedUser(currentUserId + 1)

        assertThrows<AccessDeniedException> {
            statementsService.getStatements(currentUserId, definingThemeIds)
        }

        verify(exactly = 0) { statementsRepository.findUnreactedStatements(any(), any()) }
        confirmVerified(statementsRepository)
    }

    @Test
    fun addStatement_validData_savesMappedStatement() {
        every { statementsRepository.save(capture(statementSlot)) } answers { statementSlot.captured }

        statementsService.addStatement(statementDetails)

        with(statementSlot.captured) {
            assertEquals(statementDetails.text, text)
            assertEquals(statementDetails.isSupportDefiningTheme, isSupportDefiningTheme)
            assertEquals(statementDetails.definingThemeId, definingThemeId)
            assertEquals(statementDetails.creatorUserId, creatorUserId)
        }

        verify(exactly = 1) { statementsRepository.save(any()) }
        confirmVerified(statementsRepository)
    }
}
