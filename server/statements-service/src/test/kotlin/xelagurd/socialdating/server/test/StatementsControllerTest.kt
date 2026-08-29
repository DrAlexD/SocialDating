package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.controller.StatementsController
import xelagurd.socialdating.server.service.StatementsService
import xelagurd.socialdating.server.utils.TestUtils.mockkList
import xelagurd.socialdating.server.utils.TestUtils.nextIntList
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@WebMvcTest(StatementsController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class StatementsControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var statementsService: StatementsService

    private val objectMapper = jacksonObjectMapper()

    private val userId = Random.nextInt()
    private val definingThemeIds = Random.nextIntList()
    private val statementDetails = FakeStatementsData.statementsDetails[0]
    private val statement = FakeStatementsData.statementsWithDefiningThemes[0]

    @Test
    fun getStatements_existData_ok() {
        every { statementsService.getStatements(any(), any()) } returns
                mockkList(relaxed = true)

        mockMvc.perform(
            get("/statements?currentUserId=${userId}&definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { statementsService.getStatements(any(), any()) }
        confirmVerified(statementsService)
    }

    @Test
    fun addStatement_validData_created() {
        every { statementsService.addStatement(any()) } returns statement

        mockMvc.perform(
            post("/statements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statementDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { statementsService.addStatement(any()) }
        confirmVerified(statementsService)
    }

    @Test
    fun addStatement_withoutDefiningThemes_badRequest() {
        mockMvc.perform(
            post("/statements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statementDetails.copy(definingThemes = listOf())))
        )
            .andExpect(status().isBadRequest)

        verify(exactly = 0) { statementsService.addStatement(any()) }
        confirmVerified(statementsService)
    }
}
