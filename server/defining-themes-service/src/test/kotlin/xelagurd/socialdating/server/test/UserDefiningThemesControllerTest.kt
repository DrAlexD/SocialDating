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
import xelagurd.socialdating.server.controller.UserDefiningThemesController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.service.UserDefiningThemesService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(UserDefiningThemesController::class)
@Import(NoSecurityConfig::class)
class UserDefiningThemesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    private val userId = 1

    private val userDefiningThemes = FakeDefiningThemesData.userDefiningThemes

    private val userDefiningThemeDetails = FakeDefiningThemesData.userDefiningThemesDetails[0]
    private val userDefiningTheme = userDefiningThemes[0]

    @Test
    fun getUserDefiningThemes_allData_success() {
        val expected = userDefiningThemes
        `when`(userDefiningThemesService.getUserDefiningThemes(userId)).thenReturn(expected)

        mockMvc.perform(
            get("/defining-themes/users?userId=$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getUserDefiningThemes_emptyData_error() {
        val message = "test"
        `when`(userDefiningThemesService.getUserDefiningThemes(userId)).thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/defining-themes/users?userId=$userId")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }

    @Test
    fun addUserDefiningTheme() {
        val expected = userDefiningTheme
        `when`(userDefiningThemesService.addUserDefiningTheme(userDefiningThemeDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/defining-themes/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(userDefiningThemeDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }
}