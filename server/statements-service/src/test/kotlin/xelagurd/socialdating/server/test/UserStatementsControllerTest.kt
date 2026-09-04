package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.controller.UserStatementsController
import xelagurd.socialdating.server.model.details.StatementReactionDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType
import xelagurd.socialdating.server.service.UserStatementsService

@WebMvcTest(UserStatementsController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class UserStatementsControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var userStatementsService: UserStatementsService

    private val objectMapper = jacksonObjectMapper()

    private val statementReactionDetails = StatementReactionDetails(
        userId = 1,
        statementId = 2,
        reactionType = StatementReactionType.FULL_MAINTAIN
    )

    @Test
    fun processStatementReaction_validData_noContent() {
        every { userStatementsService.processStatementReaction(any()) } just Runs

        mockMvc.perform(
            post("/statements/users/reaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statementReactionDetails))
        )
            .andExpect(status().isNoContent)

        verify(exactly = 1) { userStatementsService.processStatementReaction(any()) }
        confirmVerified(userStatementsService)
    }

}
