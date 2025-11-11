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
import xelagurd.socialdating.server.controller.StatementsController
import xelagurd.socialdating.server.service.StatementsService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@WebMvcTest(StatementsController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class StatementsControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var statementsService: StatementsService

    private val userId = 1
    private val definingThemeIds = listOf(1, 3)

    private val statements = FakeStatementsData.statements

    @Test
    fun getStatements_existData_ok() {
        every { statementsService.getStatements(userId, definingThemeIds) } returns statements

        mockMvc.perform(
            get("/statements?userId=${userId}&definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(statements)))
    }

}