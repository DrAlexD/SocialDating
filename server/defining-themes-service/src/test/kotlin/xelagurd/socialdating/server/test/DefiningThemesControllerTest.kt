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
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.controller.DefiningThemesController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.service.DefiningThemesService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(DefiningThemesController::class)
@Import(NoSecurityConfig::class)
class DefiningThemesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var definingThemesService: DefiningThemesService

    private val categoryId = 1

    private val definingThemes = FakeDefiningThemesData.definingThemes

    private val definingThemeDetails = FakeDefiningThemesData.definingThemesDetails[0]
    private val definingTheme = definingThemes[0]

    @Test
    fun getDefiningThemesByCategoryId() {
        val expected = definingThemes
        `when`(definingThemesService.getDefiningThemes(categoryId)).thenReturn(expected)

        mockMvc.perform(
            get("/defining-themes?categoryId=$categoryId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getDefiningThemes_allData_success() {
        val expected = definingThemes
        `when`(definingThemesService.getDefiningThemes()).thenReturn(expected)

        mockMvc.perform(
            get("/defining-themes")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getDefiningThemes_emptyData_error() {
        val message = "test"
        `when`(definingThemesService.getDefiningThemes()).thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/defining-themes")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }

    @Test
    fun addDefiningTheme() {
        val expected = definingTheme
        `when`(definingThemesService.addDefiningTheme(definingThemeDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/defining-themes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(definingThemeDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }
}