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
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.testcontainers.containers.PostgreSQLContainer
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.model.Category

@ActiveProfiles("dev", "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoSecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CategoriesMicroserviceTest(@param:Autowired val restTemplate: TestRestTemplate) {

    private val categoriesDetails = FakeCategoriesData.categoriesDetails
    private val categories = FakeCategoriesData.categories.take(categoriesDetails.size)

    @Order(1)
    @Test
    fun addCategory() {
        categoriesDetails.forEachIndexed { index, categoryDetails ->
            val response = restTemplate.postForEntity(
                "/categories",
                categoryDetails,
                Category::class.java
            )
            assertEquals(HttpStatus.CREATED, response.statusCode)
            assertEquals(categories[index], response.body!!)
        }
    }

    @Test
    fun getCategories() {
        val response = restTemplate.getForEntity(
            "/categories",
            Array<Category>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size, response.body!!.size)
        assertContentEquals(categories.toTypedArray(), response.body!!)
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