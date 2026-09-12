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
import xelagurd.socialdating.server.model.DefaultDataProperties.PAGE_SIZE_DEFAULT
import xelagurd.socialdating.server.model.StatementsCursor
import xelagurd.socialdating.server.model.dto.PageDto
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

    private val userId = Random.nextInt(1, Int.MAX_VALUE)
    private val definingThemeIds = Random.nextIntList()
    private val cursor = StatementsCursor.decodeOrNew(null).next(Random.nextInt(1, Int.MAX_VALUE)).encode()
    private val size = PAGE_SIZE_DEFAULT / 2
    private val statementDetails = FakeStatementsData.statementsDetails[0]
    private val statementDto = FakeStatementsData.statementDtos[0]

    private val statementsUrl =
        "/statements?currentUserId=$userId&definingThemeIds=${definingThemeIds.toRequestParams()}"

    @Test
    fun getStatements_existData_ok() {
        every { statementsService.getStatements(any(), any(), any(), any()) } returns
                PageDto(mockkList(relaxed = true), cursor)

        mockMvc.perform(get(statementsUrl))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) {
            statementsService.getStatements(userId, definingThemeIds, null, PAGE_SIZE_DEFAULT)
        }
        confirmVerified(statementsService)
    }

    @Test
    fun getStatements_noData_noContent() {
        every { statementsService.getStatements(any(), any(), any(), any()) } returns PageDto(listOf())

        mockMvc.perform(get(statementsUrl))
            .andExpect(status().isNoContent)

        verify(exactly = 1) {
            statementsService.getStatements(userId, definingThemeIds, null, PAGE_SIZE_DEFAULT)
        }
        confirmVerified(statementsService)
    }

    @Test
    fun getStatements_withCursorAndSize_passesThemToService() {
        every { statementsService.getStatements(any(), any(), any(), any()) } returns
                PageDto(mockkList(relaxed = true))

        mockMvc.perform(get("$statementsUrl&cursor=$cursor&size=$size"))
            .andExpect(status().isOk)

        verify(exactly = 1) { statementsService.getStatements(userId, definingThemeIds, cursor, size) }
        confirmVerified(statementsService)
    }

    @Test
    fun addStatement_validData_created() {
        every { statementsService.addStatement(any()) } returns statementDto

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
