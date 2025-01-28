package xelagurd.socialdating.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import xelagurd.socialdating.controller.StatementsController
import xelagurd.socialdating.dto.Statement
import xelagurd.socialdating.dto.StatementDetails
import xelagurd.socialdating.dto.StatementReactionDetails
import xelagurd.socialdating.dto.StatementReactionType
import xelagurd.socialdating.service.TestUtils.convertObjectToJsonString
import xelagurd.socialdating.service.TestUtils.toRequestParams

@WebMvcTest(StatementsController::class)
class StatementsControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockitoBean
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

    @Test
    fun getStatementsByDefiningThemeIds() {
        val expected = statements.filter { it.definingThemeId in definingThemeIds }
        `when`(statementsService.getStatements(definingThemeIds)).thenReturn(expected)

        mockMvc.perform(
            get("/api/v1/statements?definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun addStatement() {
        val expected = statements[0]
        `when`(statementsService.addStatement(statementDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/api/v1/statements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(statementDetails))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun addStatementReaction() {
        val expected = statements.filter { it.id == statementId }

        assertEquals(expected.size, 1)

        mockMvc.perform(
            post("/api/v1/statements/$statementId/reaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(statementReactionDetails))
        )
            .andExpect(status().isOk)

        verify(statementsService).addStatementReaction(statementId, statementReactionDetails)
    }
}