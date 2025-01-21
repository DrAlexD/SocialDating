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
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MicroserviceTest(@Autowired val restTemplate: TestRestTemplate) {

    private val categories = listOf(
        Category(id = 1, name = "RemoteCategory1"),
        Category(id = 2, name = "RemoteCategory2"),
        Category(id = 3, name = "RemoteCategory3")
    )
    private val categoriesDetails = listOf(
        CategoryDetails(name = "RemoteCategory1"),
        CategoryDetails(name = "RemoteCategory2"),
        CategoryDetails(name = "RemoteCategory3")
    )

    @Test
    fun addCategoriesAndGetThem() {
        val postResponse1 = restTemplate.postForEntity("/api/v1/categories", categoriesDetails[0], Category::class.java)
        assertThat(postResponse1.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse1.body!!, categories[0])

        val postResponse2 = restTemplate.postForEntity("/api/v1/categories", categoriesDetails[1], Category::class.java)
        assertThat(postResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse2.body!!, categories[1])

        val postResponse3 = restTemplate.postForEntity("/api/v1/categories", categoriesDetails[2], Category::class.java)
        assertThat(postResponse3.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse3.body!!, categories[2])

        val getResponse = restTemplate.getForEntity("/api/v1/categories", Array<Category>::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse.body!!.size, 3)
        assertContentEquals(getResponse.body!!, categories.toTypedArray())
    }
}