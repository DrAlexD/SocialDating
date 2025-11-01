package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import xelagurd.socialdating.server.controller.UserStatementsController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.additional.StatementReactionDetails
import xelagurd.socialdating.server.model.details.UserStatementDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.service.UserStatementsService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@WebMvcTest(UserStatementsController::class)
@Import(NoSecurityConfig::class)
class UserStatementsControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var userStatementsService: UserStatementsService

    private val userId = 1
    private val definingThemeIds = listOf(1, 3)

    private val userStatements = listOf(
        UserStatement(id = 1, reactionType = FULL_MAINTAIN, userId = 1, statementId = 1),
        UserStatement(id = 2, reactionType = FULL_MAINTAIN, userId = 1, statementId = 2),
        UserStatement(id = 3, reactionType = FULL_MAINTAIN, userId = 2, statementId = 3)
    )

    private val userStatementDetails = UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 1, statementId = 1)

    private val statementReactionDetails = StatementReactionDetails(
        userId = userId,
        statementId = 1,
        categoryId = 1,
        definingThemeId = 1,
        reactionType = FULL_MAINTAIN,
        isSupportDefiningTheme = true
    )

    @Test
    fun getUserStatements_allData_success() {
        val expected = userStatements.filter { it.userId == userId }
        `when`(userStatementsService.getUserStatements(userId, definingThemeIds)).thenReturn(expected)

        mockMvc.perform(
            get("/api/v1/statements/users?userId=$userId&definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getUserStatements_emptyData_error() {
        val message = "test"
        `when`(userStatementsService.getUserStatements(userId, definingThemeIds)).thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/api/v1/statements/users?userId=$userId&definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }

    @Test
    fun addUserStatement() {
        val expected = userStatements[0]
        `when`(userStatementsService.addUserStatement(userStatementDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/api/v1/statements/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(userStatementDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun addStatementReaction() {
        val expected = userStatements[0]
        `when`(userStatementsService.addStatementReaction(statementReactionDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/api/v1/statements/users/reaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(statementReactionDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }
}