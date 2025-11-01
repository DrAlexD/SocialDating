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
import xelagurd.socialdating.server.controller.UserDefiningThemesController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.details.UserDefiningThemeDetails
import xelagurd.socialdating.server.service.UserDefiningThemesService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(UserDefiningThemesController::class)
@Import(NoSecurityConfig::class)
class UserDefiningThemesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    private val userId = 1

    private val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 10, interest = 10, userId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 15, interest = 15, userId = 2, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 20, interest = 20, userId = 3, definingThemeId = 3)
    )
    private val userDefiningThemeDetails =
        UserDefiningThemeDetails(value = 10, interest = 10, userId = 1, definingThemeId = 1)

    @Test
    fun getUserDefiningThemesByUserCategoryIds_allData_success() {
        val expected = userDefiningThemes.filter { it.userId == userId }
        `when`(userDefiningThemesService.getUserDefiningThemes(userId)).thenReturn(expected)

        mockMvc.perform(
            get("/api/v1/defining-themes/users?userId=$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getUserDefiningThemesByUserCategoryIds_emptyData_error() {
        val message = "test"
        `when`(userDefiningThemesService.getUserDefiningThemes(userId)).thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/api/v1/defining-themes/users?userId=$userId")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }

    @Test
    fun addUserDefiningTheme() {
        val expected = userDefiningThemes[0]
        `when`(userDefiningThemesService.addUserDefiningTheme(userDefiningThemeDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/api/v1/defining-themes/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(userDefiningThemeDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }
}