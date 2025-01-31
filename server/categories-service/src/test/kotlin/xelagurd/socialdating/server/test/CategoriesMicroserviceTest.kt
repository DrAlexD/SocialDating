package xelagurd.socialdating.server.test

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.model.details.UserCategoryDetails

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoriesMicroserviceTest(@Autowired val restTemplate: TestRestTemplate) {

    private val userId = 1

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

    private val userCategories = listOf(
        UserCategory(id = 1, interest = 10, userId = 1, categoryId = 1),
        UserCategory(id = 2, interest = 15, userId = 1, categoryId = 2),
        UserCategory(id = 3, interest = 20, userId = 2, categoryId = 3)
    )
    private val userCategoriesDetails = listOf(
        UserCategoryDetails(interest = 10, userId = 1, categoryId = 1),
        UserCategoryDetails(interest = 15, userId = 1, categoryId = 2),
        UserCategoryDetails(interest = 20, userId = 2, categoryId = 3)
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

    @Test
    fun addUserCategoriesAndGetThem() {
        val postResponse1 = restTemplate.postForEntity(
            "/api/v1/categories/users",
            userCategoriesDetails[0],
            UserCategory::class.java
        )
        assertThat(postResponse1.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse1.body!!, userCategories[0])

        val postResponse2 = restTemplate.postForEntity(
            "/api/v1/categories/users",
            userCategoriesDetails[1],
            UserCategory::class.java
        )
        assertThat(postResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse2.body!!, userCategories[1])

        val postResponse3 = restTemplate.postForEntity(
            "/api/v1/categories/users",
            userCategoriesDetails[2],
            UserCategory::class.java
        )
        assertThat(postResponse3.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse3.body!!, userCategories[2])

        val getResponse = restTemplate.getForEntity("/api/v1/categories/users/$userId", Array<UserCategory>::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse.body!!.size, 2)
        assertContentEquals(getResponse.body!!, arrayOf(userCategories[0], userCategories[1]))
    }
}