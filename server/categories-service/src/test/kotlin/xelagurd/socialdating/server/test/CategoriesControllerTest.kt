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
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.controller.CategoriesController
import xelagurd.socialdating.server.exception.NoDataFoundException
import xelagurd.socialdating.server.service.CategoriesService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(CategoriesController::class)
@Import(NoSecurityConfig::class)
class CategoriesControllerTest(@param:Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var categoriesService: CategoriesService

    private val categories = FakeCategoriesData.categories

    private val categoryDetails = FakeCategoriesData.categoriesDetails[0]
    private val category = categories[0]

    @Test
    fun getCategories_allData_success() {
        val expected = categories
        `when`(categoriesService.getCategories()).thenReturn(expected)

        mockMvc.perform(
            get("/api/v1/categories")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }

    @Test
    fun getCategories_emptyData_error() {
        val message = "test"
        `when`(categoriesService.getCategories()).thenThrow(NoDataFoundException(message))

        mockMvc.perform(
            get("/api/v1/categories")
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string(message))
    }

    @Test
    fun addCategory() {
        val expected = category
        `when`(categoriesService.addCategory(categoryDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(categoryDetails))
        )
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }
}