package xelagurd.socialdating.server.test

import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.common.DefiningThemeReactionDetails
import xelagurd.socialdating.server.model.common.UserDefiningThemesUpdateDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType
import xelagurd.socialdating.server.repository.StatementDefiningThemesRepository
import xelagurd.socialdating.server.repository.UserStatementsRepository
import xelagurd.socialdating.server.service.StatementsKafkaProducer
import xelagurd.socialdating.server.service.UserStatementsService

@ExtendWith(MockKExtension::class)
class UserStatementsServiceUnitTest {

    @MockK
    private lateinit var userStatementsRepository: UserStatementsRepository

    @MockK
    private lateinit var statementDefiningThemesRepository: StatementDefiningThemesRepository

    @MockK
    private lateinit var kafkaProducer: StatementsKafkaProducer

    @InjectMockKs
    private lateinit var userStatementsService: UserStatementsService

    private val statementReactionDetails = StatementReactionDetails(
        userId = 1,
        statementId = 5,
        reactionType = StatementReactionType.FULL_MAINTAIN
    )

    private val statementDefiningThemes = FakeStatementsData.statementDefiningThemes
        .filter { it.statementId == statementReactionDetails.statementId }
    private val expectedDefiningThemes = listOf(
        DefiningThemeReactionDetails(1, true),
        DefiningThemeReactionDetails(2, false)
    )

    private val userStatementSlot = slot<UserStatement>()
    private val updateDetailsSlot = slot<UserDefiningThemesUpdateDetails>()

    @Test
    fun processStatementReaction_validData_savesUserStatementAndProducesEventWithAllDefiningThemes() {
        every { statementDefiningThemesRepository.findAllByStatementId(any()) } returns statementDefiningThemes
        every { userStatementsRepository.save(capture(userStatementSlot)) } returns mockk()
        every { kafkaProducer.updateUserDefiningThemes(capture(updateDetailsSlot)) } just Runs

        userStatementsService.processStatementReaction(statementReactionDetails)

        with(userStatementSlot.captured) {
            assertEquals(statementReactionDetails.reactionType, reactionType)
            assertEquals(statementReactionDetails.userId, userId)
            assertEquals(statementReactionDetails.statementId, statementId)
        }
        with(updateDetailsSlot.captured) {
            assertEquals(statementReactionDetails.userId, userId)
            assertEquals(statementReactionDetails.reactionType, reactionType)
            assertEquals(expectedDefiningThemes, definingThemes)
        }

        verify(exactly = 1) {
            statementDefiningThemesRepository.findAllByStatementId(statementReactionDetails.statementId)
        }
        verify(exactly = 1) { userStatementsRepository.save(any()) }
        verify(exactly = 1) { kafkaProducer.updateUserDefiningThemes(any()) }
        confirmVerified(userStatementsRepository, statementDefiningThemesRepository, kafkaProducer)
    }
}
