package xelagurd.socialdating.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import xelagurd.socialdating.dto.Category
import xelagurd.socialdating.dto.CategoryDetails
import kotlin.test.assertEquals

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MicroserviceTest(@Autowired val restTemplate: TestRestTemplate) {

    private val category = Category(id = 1, name = "RemoteCategory1")
    private val categoryDetails = CategoryDetails(name = "RemoteCategory1")

    @Test
    fun addCategoryAndGetIt() {
        val postResponse = restTemplate.postForEntity("/api/v1/categories", categoryDetails, Category::class.java)
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse.body!!, category)

        val getResponse = restTemplate.getForEntity("/api/v1/categories", Array<Category>::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse.body!!.size, 1)
        assertEquals(getResponse.body!![0], category)
    }
}