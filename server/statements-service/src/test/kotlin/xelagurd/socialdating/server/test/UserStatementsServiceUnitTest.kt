package xelagurd.socialdating.server.test

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.repository.UserStatementsRepository
import xelagurd.socialdating.server.service.StatementsKafkaProducer
import xelagurd.socialdating.server.service.UserStatementsService

@ExtendWith(MockKExtension::class)
class UserStatementsServiceUnitTest {

    @MockK
    private lateinit var userStatementsRepository: UserStatementsRepository

    @MockK
    private lateinit var kafkaProducer: StatementsKafkaProducer

    @InjectMockKs
    private lateinit var userStatementsService: UserStatementsService

    private val userStatement = FakeStatementsData.userStatements[0]
    private val statementReactionDetails = FakeStatementsData.statementReactionDetails

    @Test
    fun processStatementReaction() {
        every { userStatementsRepository.save(statementReactionDetails.toUserStatement()) } returns userStatement
        every { kafkaProducer.updateUserCategory(statementReactionDetails.toUserCategoryUpdateDetails()) } just Runs

        val result = userStatementsService.processStatementReaction(statementReactionDetails)

        assertEquals(userStatement, result)
    }

}