package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
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
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.details.UserDefiningThemeDetails
import xelagurd.socialdating.server.service.UserDefiningThemesService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@WebMvcTest(UserDefiningThemesController::class)
class UserDefiningThemesControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    private val userCategoryIds = listOf(1, 3)

    private val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 10, interest = 10, userCategoryId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 15, interest = 15, userCategoryId = 2, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 20, interest = 20, userCategoryId = 3, definingThemeId = 3)
    )
    private val userDefiningThemeDetails =
        UserDefiningThemeDetails(value = 10, interest = 10, userCategoryId = 1, definingThemeId = 1)

    @Test
    fun getUserDefiningThemesByUserCategoryIds() {
        val expected = userDefiningThemes.filter { it.userCategoryId in userCategoryIds }
        `when`(userDefiningThemesService.getUserDefiningThemes(userCategoryIds)).thenReturn(expected)

        mockMvc.perform(
            get("/api/v1/defining-themes/users?userCategoryIds=${userCategoryIds.toRequestParams()}")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
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