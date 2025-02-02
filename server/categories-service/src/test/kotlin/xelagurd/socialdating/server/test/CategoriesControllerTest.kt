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
import xelagurd.socialdating.server.controller.CategoriesController
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.service.CategoriesService
import xelagurd.socialdating.server.utils.TestUtils.convertObjectToJsonString

@WebMvcTest(CategoriesController::class)
class CategoriesControllerTest(@Autowired private val mockMvc: MockMvc) {

    @MockitoBean
    private lateinit var categoriesService: CategoriesService

    private val categories = listOf(
        Category(id = 1, name = "RemoteCategory1"),
        Category(id = 2, name = "RemoteCategory2"),
        Category(id = 3, name = "RemoteCategory3")
    )
    private val categoryDetails = CategoryDetails(name = "RemoteCategory1")

    @Test
    fun getCategories() {
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
    fun addCategory() {
        val expected = categories[0]
        `when`(categoriesService.addCategory(categoryDetails)).thenReturn(expected)

        mockMvc.perform(
            post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(categoryDetails))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(convertObjectToJsonString(expected)))
    }
}