package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.controller.UserStatementsController
import xelagurd.socialdating.server.service.UserStatementsService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@WebMvcTest(UserStatementsController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class UserStatementsControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var userStatementsService: UserStatementsService

    private val userId = 1
    private val definingThemeIds = listOf(1, 3)

    private val userStatements = FakeStatementsData.userStatements

    @Test
    fun getUserStatements_existData_ok() {
        every { userStatementsService.getUserStatements(userId, definingThemeIds) } returns userStatements

        mockMvc.perform(
            get("/statements/users?userId=$userId&definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(userStatements)))
    }

}