package xelagurd.socialdating

import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import xelagurd.socialdating.controller.DefiningThemesController
import xelagurd.socialdating.dto.DefiningTheme
import xelagurd.socialdating.dto.DefiningThemeDetails
import xelagurd.socialdating.TestUtils.convertObjectToJsonString
import xelagurd.socialdating.TestUtils.toRequestParams
import xelagurd.socialdating.service.DefiningThemesService

@WebMvcTest(DefiningThemesController::class)
class DefiningThemesControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var definingThemesService: DefiningThemesService

    private val categoryId = 1
    private val categoryIds = listOf(1, 3)

    private val definingThemes = listOf(
        DefiningTheme(id = 1, name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningTheme(id = 2, name = "RemoteDefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningTheme(id = 3, name = "RemoteDefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 3)
    )
    private val definingThemeDetails =
        DefiningThemeDetails(name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1)

    @Test
    fun getDefiningThemesByCategoryId() {
        val expected = definingThemes.filter { it.categoryId == categoryId }
        `when`(definingThemesService.getDefiningThemes(listOf(categoryId))).thenReturn(expected)

        mockMvc.perform(
            get("/api/v1/defining-themes?categoryIds=$categoryId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getDefiningThemesByCategoryIds() {
        val expected = definingThemes.filter { it.categoryId in categoryIds }
        `when`(definingThemesService.getDefiningThemes(categoryIds)).thenReturn(expected)

        mockMvc.perform(
            get("/api/v1/defining-themes?categoryIds=${categoryIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun addDefiningTheme() {
        val expected = definingThemes[0]
        `when`(definingThemesService.addDefiningTheme(definingThemeDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/api/v1/defining-themes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(definingThemeDetails))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }
}