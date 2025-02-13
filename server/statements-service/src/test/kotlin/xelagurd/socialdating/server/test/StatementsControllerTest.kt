package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import xelagurd.socialdating.server.controller.StatementsController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType
import xelagurd.socialdating.server.service.StatementsService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

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
        userOrUserCategoryId = userId,
        categoryId = 1,
        definingThemeId = 1,
        reactionType = StatementReactionType.FULL_MAINTAIN,
        isSupportDefiningTheme = true
    )

    @Test
    fun getStatementsByDefiningThemeIds_allData_success() {
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
    fun getStatementsByDefiningThemeIds_emptyData_error() {
        val message = "test"
        `when`(statementsService.getStatements(definingThemeIds)).thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/api/v1/statements?definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
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
            .andExpect(status().isCreated)
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
            .andExpect(status().isNoContent)

        verify(statementsService).addStatementReaction(statementId, statementReactionDetails)
    }
}