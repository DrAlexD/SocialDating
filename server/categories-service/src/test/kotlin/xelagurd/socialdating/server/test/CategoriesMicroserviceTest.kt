package xelagurd.socialdating.server.test

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.PostgreSQLContainer
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.FakeCategoriesData.filterByUserId
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.UserCategory

@ActiveProfiles("dev", "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoSecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoriesMicroserviceTest(@param:Autowired val restTemplate: TestRestTemplate) {

    private val userId = 1

    private val categoriesDetails = FakeCategoriesData.categoriesDetails
    private val categories = FakeCategoriesData.categories.take(categoriesDetails.size)
    private val userCategoriesDetails = FakeCategoriesData.userCategoriesDetails
    private val userCategories = FakeCategoriesData.userCategories.take(userCategoriesDetails.size)

    init {
        addCategories()
        addUserCategories()
    }

    @Test
    fun testGetCategories() {
        getCategories()
    }

    @Test
    fun testGetUserCategories() {
        getUserCategories()
    }

    private fun addCategories() {
        categoriesDetails.forEachIndexed { index, categoryDetails ->
            val response = restTemplate.postForEntity(
                "/api/v1/categories",
                categoryDetails,
                Category::class.java
            )
            assertEquals(HttpStatus.CREATED, response.statusCode)
            assertEquals(categories[index], response.body!!)
        }
    }

    private fun addUserCategories() {
        userCategoriesDetails.forEachIndexed { index, userCategoryDetails ->
            val response = restTemplate.postForEntity(
                "/api/v1/categories/users",
                userCategoryDetails,
                UserCategory::class.java
            )
            assertEquals(HttpStatus.CREATED, response.statusCode)
            assertEquals(userCategories[index], response.body!!)
        }
    }

    private fun getCategories() {
        val response = restTemplate.getForEntity(
            "/api/v1/categories",
            Array<Category>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size, response.body!!.size)
        assertContentEquals(categories.toTypedArray(), response.body!!)
    }

    private fun getUserCategories() {
        val expected = userCategories.filterByUserId(userId)
        val response = restTemplate.getForEntity(
            "/api/v1/categories/users?userId=$userId",
            Array<UserCategory>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected.size, response.body!!.size)
        assertContentEquals(expected.toTypedArray(), response.body!!)
    }

    companion object {
        @ServiceConnection
        val postgresContainer = PostgreSQLContainer("postgres:18")
            .apply {
                withDatabaseName("test_db")
                withUsername("test_user")
                withPassword("test_password")
            }
    }
}