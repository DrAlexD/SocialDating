package xelagurd.socialdating.service

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
import xelagurd.socialdating.controller.CategoriesController
import xelagurd.socialdating.dto.Category
import xelagurd.socialdating.dto.CategoryDetails
import xelagurd.socialdating.service.TestUtils.convertObjectToJsonString

@WebMvcTest(CategoriesController::class)
class CategoriesControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var categoriesService: CategoriesService

    private val category = Category(id = 1, name = "RemoteCategory1")
    private val categoryDetails = CategoryDetails(name = "RemoteCategory1")

    @Test
    fun getCategories() {
        `when`(categoriesService.getCategories()).thenReturn(listOf(category))

        mockMvc.perform(
            get("/api/v1/categories")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(listOf(category))))
    }

    @Test
    fun addCategory() {
        `when`(categoriesService.addCategory(categoryDetails)).thenReturn(category)

        mockMvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(categoryDetails))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(category)))
    }
}