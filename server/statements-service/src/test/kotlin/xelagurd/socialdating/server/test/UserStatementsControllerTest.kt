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
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.controller.UserStatementsController
import xelagurd.socialdating.server.exception.NoDataFoundException
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

    private val userStatements = FakeStatementsData.userStatements

    private val userStatementDetails = FakeStatementsData.userStatementsDetails[0]
    private val userStatement = userStatements[0]

    private val statementReactionDetails = FakeStatementsData.statementReactionDetails

    @Test
    fun getUserStatements_allData_success() {
        val expected = userStatements
        `when`(userStatementsService.getUserStatements(userId, definingThemeIds)).thenReturn(expected)

        mockMvc.perform(
            get("/statements/users?userId=$userId&definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getUserStatements_emptyData_error() {
        val message = "test"
        `when`(userStatementsService.getUserStatements(userId, definingThemeIds))
            .thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/statements/users?userId=$userId&definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }

    @Test
    fun addUserStatement() {
        val expected = userStatement
        `when`(userStatementsService.addUserStatement(userStatementDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/statements/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(userStatementDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun processStatementReaction() {
        val expected = userStatement
        `when`(userStatementsService.processStatementReaction(statementReactionDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/statements/users/reaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(statementReactionDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }
}