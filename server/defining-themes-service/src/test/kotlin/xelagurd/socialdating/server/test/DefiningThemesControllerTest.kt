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
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.controller.DefiningThemesController
import xelagurd.socialdating.server.service.DefiningThemesService
import xelagurd.socialdating.server.utils.TestUtils.mockkList
import xelagurd.socialdating.server.utils.TestUtils.nextIntList
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@WebMvcTest(DefiningThemesController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class DefiningThemesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var definingThemesService: DefiningThemesService

    private val objectMapper = jacksonObjectMapper()

    private val categoryId = Random.nextInt(1, Int.MAX_VALUE)
    private val definingThemeIds = Random.nextIntList()
    private val definingThemeDetails = FakeDefiningThemesData.definingThemesDetails[0]
    private val definingThemeDto = FakeDefiningThemesData.definingThemeDtos[0]

    @Test
    fun getDefiningThemes_existData_ok() {
        every { definingThemesService.getDefiningThemes() } returns mockkList(relaxed = true)

        mockMvc.perform(
            get("/defining-themes")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { definingThemesService.getDefiningThemes() }
        confirmVerified(definingThemesService)
    }

    @Test
    fun getDefiningThemes_withIdsAndCategoryId_ok() {
        every { definingThemesService.getDefiningThemes(any(), any()) } returns mockkList(relaxed = true)

        mockMvc.perform(
            get("/defining-themes?definingThemeIds=${definingThemeIds.toRequestParams()}&categoryId=$categoryId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { definingThemesService.getDefiningThemes(definingThemeIds, categoryId) }
        confirmVerified(definingThemesService)
    }

    @Test
    fun addDefiningTheme_validData_created() {
        every { definingThemesService.addDefiningTheme(any()) } returns definingThemeDto

        mockMvc.perform(
            post("/defining-themes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(definingThemeDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))

        verify(exactly = 1) { definingThemesService.addDefiningTheme(any()) }
        confirmVerified(definingThemesService)
    }
}
