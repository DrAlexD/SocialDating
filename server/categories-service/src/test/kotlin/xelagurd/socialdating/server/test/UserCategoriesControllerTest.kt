package xelagurd.socialdating.server.test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.controller.UserCategoriesController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.service.UserCategoriesService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(UserCategoriesController::class)
@Import(NoSecurityConfig::class)
class UserCategoriesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var userCategoriesService: UserCategoriesService

    private val userId = 1

    private val userCategories = FakeCategoriesData.userCategories

    @Test
    fun getUserCategories_allData_success() {
        val expected = userCategories
        `when`(userCategoriesService.getUserCategories(userId)).thenReturn(expected)

        mockMvc.perform(
            get("/categories/users?userId=$userId")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getUserCategories_emptyData_error() {
        val message = "test"
        `when`(userCategoriesService.getUserCategories(userId)).thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/categories/users?userId=$userId")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }

}