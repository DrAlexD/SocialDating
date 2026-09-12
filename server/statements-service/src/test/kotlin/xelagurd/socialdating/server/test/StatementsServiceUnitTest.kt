package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.springframework.data.domain.Limit
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
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.exception.InvalidDataException
import xelagurd.socialdating.server.model.DefaultDataProperties.PAGE_SIZE_DEFAULT
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.StatementDefiningTheme
import xelagurd.socialdating.server.model.StatementsCursor
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

    private val seedSlot = slot<String>()
    private val lastOrderKeySlot = slot<String>()
    private val limitSlot = slot<Limit>()

    @AfterEach
    fun clearSecurityContext() {
        SecurityContextHolder.clearContext()
    }

    private fun setAuthenticatedUser(userId: Int) {
        val authentication = mockk<Authentication>()
        every { authentication.principal } returns userId
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun mockUnreactedStatements(foundStatements: List<Statement>) {
        every {
            statementsRepository.findUnreactedStatements(
                any(), any(), capture(seedSlot), capture(lastOrderKeySlot), capture(limitSlot)
            )
        } returns foundStatements

        if (foundStatements.isNotEmpty()) {
            every { statementDefiningThemesRepository.findAllByStatementIdIn(any()) } returns statementDefiningThemes
        }
    }

    @Test
    fun getStatements_authorized_returnsUnreactedStatementsWithDefiningThemes() {
        setAuthenticatedUser(currentUserId)
        mockUnreactedStatements(statements)

        val result = statementsService.getStatements(currentUserId, definingThemeIds)

        assertEquals(statements.size, result.content.size)
        assertEquals(statements.map { it.id }, result.content.map { it.id })
        assertEquals(multiThemeDefiningThemeDtos, result.content.last().definingThemes)

        verify(exactly = 1) {
            statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds, any(), any(), any())
        }
        verify(exactly = 1) { statementDefiningThemesRepository.findAllByStatementIdIn(statements.map { it.id!! }) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun getStatements_noCursor_startsNewOrderFromTheBeginning() {
        setAuthenticatedUser(currentUserId)
        mockUnreactedStatements(statements)

        statementsService.getStatements(currentUserId, definingThemeIds)
        val firstSeed = seedSlot.captured

        statementsService.getStatements(currentUserId, definingThemeIds)
        val secondSeed = seedSlot.captured

        assertEquals("", lastOrderKeySlot.captured)
        assertEquals(PAGE_SIZE_DEFAULT, limitSlot.captured.max())
        assertNotEquals(firstSeed, secondSeed)

        verify(exactly = 2) {
            statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds, any(), any(), any())
        }
        verify(exactly = 2) { statementDefiningThemesRepository.findAllByStatementIdIn(any()) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun getStatements_fullPage_returnsNextCursorOfTheSameOrder() {
        setAuthenticatedUser(currentUserId)
        mockUnreactedStatements(statements)

        val result = statementsService.getStatements(currentUserId, definingThemeIds, size = statements.size)

        assertNotNull(result.nextCursor)

        val nextCursor = StatementsCursor.decodeOrNew(result.nextCursor!!)
        assertEquals(seedSlot.captured, nextCursor.seed)
        assertEquals(StatementsCursor.orderKey(statements.last().id!!, seedSlot.captured), nextCursor.lastOrderKey)

        verify(exactly = 1) {
            statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds, any(), any(), any())
        }
        verify(exactly = 1) { statementDefiningThemesRepository.findAllByStatementIdIn(any()) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun getStatements_partialPage_returnsNoNextCursor() {
        setAuthenticatedUser(currentUserId)
        mockUnreactedStatements(statements)

        val result = statementsService.getStatements(currentUserId, definingThemeIds, size = statements.size + 1)

        assertNull(result.nextCursor)

        verify(exactly = 1) {
            statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds, any(), any(), any())
        }
        verify(exactly = 1) { statementDefiningThemesRepository.findAllByStatementIdIn(any()) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun getStatements_withCursor_continuesTheSameOrder() {
        setAuthenticatedUser(currentUserId)
        mockUnreactedStatements(statements)

        val cursor = StatementsCursor.decodeOrNew(null).next(statements.last().id!!)

        statementsService.getStatements(currentUserId, definingThemeIds, cursor.encode())

        assertEquals(cursor.seed, seedSlot.captured)
        assertEquals(cursor.lastOrderKey, lastOrderKeySlot.captured)

        verify(exactly = 1) {
            statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds, any(), any(), any())
        }
        verify(exactly = 1) { statementDefiningThemesRepository.findAllByStatementIdIn(any()) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun getStatements_wrongCursor_throwsInvalidData() {
        setAuthenticatedUser(currentUserId)

        assertThrows<InvalidDataException> {
            statementsService.getStatements(currentUserId, definingThemeIds, "wrongCursor")
        }

        verify(exactly = 0) { statementsRepository.findUnreactedStatements(any(), any(), any(), any(), any()) }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun getStatements_noUnreactedStatements_returnsEmptyWithoutDefiningThemesRequest() {
        setAuthenticatedUser(currentUserId)
        mockUnreactedStatements(listOf())

        val result = statementsService.getStatements(currentUserId, definingThemeIds)

        assertEquals(listOf<StatementDto>(), result.content)
        assertNull(result.nextCursor)

        verify(exactly = 1) {
            statementsRepository.findUnreactedStatements(currentUserId, definingThemeIds, any(), any(), any())
        }
        confirmVerified(statementsRepository, statementDefiningThemesRepository)
    }

    @Test
    fun getStatements_anotherUser_throwsAccessDenied() {
        setAuthenticatedUser(currentUserId + 1)

        assertThrows<AccessDeniedException> {
            statementsService.getStatements(currentUserId, definingThemeIds)
        }

        verify(exactly = 0) { statementsRepository.findUnreactedStatements(any(), any(), any(), any(), any()) }
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
