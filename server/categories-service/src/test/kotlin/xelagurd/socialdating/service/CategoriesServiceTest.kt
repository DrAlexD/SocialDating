package xelagurd.socialdating.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.bean.override.mockito.MockitoBean
import xelagurd.socialdating.dto.Category
import xelagurd.socialdating.dto.CategoryDetails
import xelagurd.socialdating.repository.CategoriesRepository
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class CategoriesServiceTest(@Autowired val restTemplate: TestRestTemplate) {

    @MockitoBean
    private lateinit var categoriesRepository: CategoriesRepository

    private val category = Category(id = 1, name = "RemoteCategory1")
    private val categoryDetails = CategoryDetails(name = "RemoteCategory1")

    @Test
    fun addCategoryAndGetIt() {
        `when`(categoriesRepository.findAll()).thenReturn(listOf(category))
        `when`(categoriesRepository.save(categoryDetails.toCategory())).thenReturn(category)

        restTemplate.postForEntity("/api/v1/categories", categoryDetails, Category::class.java)
        val response = restTemplate.getForEntity("/api/v1/categories", Array<Category>::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(response.body!![0], category)
    }
}