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
import xelagurd.socialdating.server.controller.UserCategoriesController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.details.UserCategoryDetails
import xelagurd.socialdating.server.service.UserCategoriesService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(UserCategoriesController::class)
@Import(NoSecurityConfig::class)
class UserCategoriesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var userCategoriesService: UserCategoriesService

    private val userId = 1

    private val userCategories = listOf(
        UserCategory(id = 1, interest = 10, userId = 1, categoryId = 1),
        UserCategory(id = 2, interest = 15, userId = 1, categoryId = 2),
        UserCategory(id = 3, interest = 20, userId = 2, categoryId = 3)
    )

    private val userCategoryDetails = UserCategoryDetails(interest = 10, userId = 1, categoryId = 1)

    @Test
    fun getUserCategories_allData_success() {
        val expected = userCategories.filter { it.userId == userId }
        `when`(userCategoriesService.getUserCategories(userId)).thenReturn(expected)

        mockMvc.perform(
            get("/api/v1/categories/users/$userId")
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
            get("/api/v1/categories/users/$userId")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }

    @Test
    fun addUserCategory() {
        val expected = userCategories[0]
        `when`(userCategoriesService.addUserCategory(userCategoryDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/api/v1/categories/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(userCategoryDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }
}