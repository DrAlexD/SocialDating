package xelagurd.socialdating.server.test

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.model.details.CategoryDetails
import xelagurd.socialdating.server.model.details.UserCategoryDetails

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoSecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoriesMicroserviceTest(@param:Autowired val restTemplate: TestRestTemplate) {

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

    init {
        val postResponse1 = restTemplate.postForEntity("/api/v1/categories", categoriesDetails[0], Category::class.java)
        assertThat(postResponse1.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse1.body!!, categories[0])

        val postResponse2 = restTemplate.postForEntity("/api/v1/categories", categoriesDetails[1], Category::class.java)
        assertThat(postResponse2.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse2.body!!, categories[1])

        val postResponse3 = restTemplate.postForEntity("/api/v1/categories", categoriesDetails[2], Category::class.java)
        assertThat(postResponse3.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse3.body!!, categories[2])

        val postResponse4 = restTemplate.postForEntity(
            "/api/v1/categories/users",
            userCategoriesDetails[0],
            UserCategory::class.java
        )
        assertThat(postResponse4.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse4.body!!, userCategories[0])

        val postResponse5 = restTemplate.postForEntity(
            "/api/v1/categories/users",
            userCategoriesDetails[1],
            UserCategory::class.java
        )
        assertThat(postResponse5.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse5.body!!, userCategories[1])

        val postResponse6 = restTemplate.postForEntity(
            "/api/v1/categories/users",
            userCategoriesDetails[2],
            UserCategory::class.java
        )
        assertThat(postResponse6.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse6.body!!, userCategories[2])
    }

    @Test
    fun getCategories() {
        val getResponse = restTemplate.getForEntity("/api/v1/categories", Array<Category>::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse.body!!.size, 3)
        assertContentEquals(getResponse.body!!, categories.toTypedArray())
    }

    @Test
    fun addCategory_notUniqueName_error() {
        val categoryDetails = CategoryDetails(name = "RemoteCategory1")
        val postResponse = restTemplate.postForEntity("/api/v1/categories", categoryDetails, String::class.java)
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertEquals(postResponse.body!!, "Category with 'RemoteCategory1' name already exists")
    }

    @Test
    fun addCategory_emptyName_error() {
        val categoryDetails = CategoryDetails(name = "")
        val postResponse = restTemplate.postForEntity("/api/v1/categories", categoryDetails, String::class.java)
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertEquals(postResponse.body!!, "'Name' must not be blank")
    }

    @Test
    fun getUserCategories() {
        val getResponse = restTemplate.getForEntity("/api/v1/categories/users?userId=$userId", Array<UserCategory>::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse.body!!.size, 2)
        assertContentEquals(getResponse.body!!, arrayOf(userCategories[0], userCategories[1]))
    }

    @Test
    fun addUserCategory_wrongInterest_error() {
        val userCategoryDetails = UserCategoryDetails(interest = -5, categoryId = 1, userId = 1)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/categories/users",
            userCategoryDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertEquals(postResponse.body!!, "'Interest' must be greater than or equal to 0")
    }

    @Test
    fun addUserCategory_wrongCategoryId_error() {
        val userCategoryDetails = UserCategoryDetails(interest = 10, categoryId = -4, userId = 1)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/categories/users",
            userCategoryDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertEquals(postResponse.body!!, "'CategoryId' must be greater than or equal to 1")
    }

    @Test
    fun addUserCategory_wrongInterestAndCategoryId_error() {
        val userCategoryDetails = UserCategoryDetails(interest = -5, categoryId = -4, userId = 1)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/categories/users",
            userCategoryDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertEquals(
            postResponse.body!!,
            "'CategoryId' must be greater than or equal to 1; 'Interest' must be greater than or equal to 0"
        )
    }
}