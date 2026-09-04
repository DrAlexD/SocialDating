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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.StatementDefiningTheme
import xelagurd.socialdating.server.model.details.DefiningThemeReactionDetails
import xelagurd.socialdating.server.model.dto.DefiningThemeReactionDto
import xelagurd.socialdating.server.model.dto.StatementDto
import xelagurd.socialdating.server.repository.StatementDefiningThemesRepository
import xelagurd.socialdating.server.repository.StatementsRepository
import xelagurd.socialdating.server.service.StatementsService
import xelagurd.socialdating.server.utils.TestUtils.nextIntList

@ExtendWith(MockKExtension::class)
class StatementsServiceUnitTest {

    @MockK
    private lateinit var statementsRepository: StatementsRepository

    @MockK
    private lateinit var statementDefiningThemesRepository: StatementDefiningThemesRepository

    @InjectMockKs
    private lateinit var statementsService: StatementsService

    private val currentUserId = Random.nextInt(1, Int.MAX_VALUE)
    private val definingThemeIds = Random.nextIntList()

    private val statements = FakeStatementsData.statements.take(5)
    private val statementDefiningThemes = FakeStatementsData.statementDefiningThemes
        .filter { it.statementId <= 5 }
    private val multiThemeDefiningThemeDtos = listOf(
        DefiningThemeReactionDto(1, true),
        DefiningThemeReactionDto(2, false)
    )

    private val statementDetails = FakeStatementsData.statementsDetails[4]
    private val statementSlot = slot<Statement>()
    private val definingThemesSlot = slot<List<StatementDefiningTheme>>()

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
    fun getStatements_authorized_returnsUnreactedStatementsWithDefiningThemes() {
        setAuthenticatedUser(currentUserId)
        every { statementsRepository.findUnreactedStatements(any(), any()) } returns statements
        every { statementDefiningThemesRepository.findAllByStatementIdIn(any()) } returns statementDefiningThemes

        val result = statementsService.getStatements(currentUserId, definingThemeIds)

        assertEquals(statements.size, result.size)
        assertEquals(statements.map { it.id }, result.map { it.id })
        assertEquals(multiThemeDefiningThemeDtos, result.last().definingThemes)

        verify(exactly = 1) { statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds) }
        verify(exactly = 1) { statementDefiningThemesRepository.findAllByStatementIdIn(statements.map { it.id!! }) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun getStatements_noUnreactedStatements_returnsEmptyWithoutDefiningThemesRequest() {
        setAuthenticatedUser(currentUserId)
        every { statementsRepository.findUnreactedStatements(any(), any()) } returns emptyList()

        val result = statementsService.getStatements(currentUserId, definingThemeIds)

        assertEquals(listOf<StatementDto>(), result)

        verify(exactly = 1) { statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun getStatements_anotherUser_throwsAccessDenied() {
        setAuthenticatedUser(currentUserId + 1)

        assertThrows<AccessDeniedException> {
            statementsService.getStatements(currentUserId, definingThemeIds)
        }

        verify(exactly = 0) { statementsRepository.findUnreactedStatements(any(), any()) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun addStatement_validData_savesMappedStatementWithDefiningThemes() {
        every { statementsRepository.save(capture(statementSlot)) } answers {
            statementSlot.captured.apply { id = 5 }
        }
        every { statementDefiningThemesRepository.saveAll(capture(definingThemesSlot)) } answers {
            definingThemesSlot.captured
        }

        val result = statementsService.addStatement(statementDetails)

        with(statementSlot.captured) {
            assertEquals(statementDetails.text, textEn)
            assertNull(textRu)
            assertEquals(statementDetails.creatorUserId, creatorUserId)
        }
        assertEquals(statementDetails.definingThemes.size, definingThemesSlot.captured.size)
        definingThemesSlot.captured.forEachIndexed { index, statementDefiningTheme ->
            with(statementDefiningTheme) {
                assertEquals(5, statementId)
                assertEquals(statementDetails.definingThemes[index].definingThemeId, definingThemeId)
                assertEquals(statementDetails.definingThemes[index].isSupportDefiningTheme, isSupportDefiningTheme)
            }
        }
        assertEquals(multiThemeDefiningThemeDtos, result.definingThemes)

        verify(exactly = 1) { statementsRepository.save(any()) }
        verify(exactly = 1) { statementDefiningThemesRepository.saveAll(any<List<StatementDefiningTheme>>()) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun addStatement_duplicatedDefiningThemes_throwsIllegalArgument() {
        val duplicatedDetails = statementDetails.copy(
            definingThemes = listOf(
                DefiningThemeReactionDetails(1, true),
                DefiningThemeReactionDetails(1, false)
            )
        )

        assertThrows<IllegalArgumentException> {
            statementsService.addStatement(duplicatedDetails)
        }

        verify(exactly = 0) { statementsRepository.save(any()) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }
}
