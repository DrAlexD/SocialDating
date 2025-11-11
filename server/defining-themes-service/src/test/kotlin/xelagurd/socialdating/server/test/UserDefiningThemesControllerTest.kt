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
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.controller.UserDefiningThemesController
import xelagurd.socialdating.server.service.UserDefiningThemesService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(UserDefiningThemesController::class)
@Import(NoSecurityConfig::class)
@ExtendWith(MockKExtension::class)
class UserDefiningThemesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockkBean
    private lateinit var userDefiningThemesService: UserDefiningThemesService

    private val userId = 1

    private val userDefiningThemes = FakeDefiningThemesData.userDefiningThemes

    @Test
    fun getUserDefiningThemes_existData_ok() {
        every { userDefiningThemesService.getUserDefiningThemes(userId) } returns userDefiningThemes

        mockMvc.perform(
            get("/defining-themes/users?userId=$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(userDefiningThemes)))
    }

}