package xelagurd.socialdating.server.test

import kotlin.random.Random
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import com.ninjasquad.springmockk.MockkBean
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
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

    private val userId = Random.nextInt()
    private val definingThemeIds = Random.nextIntList()

    @Test
    fun getStatements_existData_ok() {
        every { statementsService.getStatements(any(), any()) } returns
                mockkList(relaxed = true)

        mockMvc.perform(
            get("/statements?userId=${userId}&definingThemeIds=${definingThemeIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { statementsService.getStatements(any(), any()) }
        confirmVerified(statementsService)
    }

}