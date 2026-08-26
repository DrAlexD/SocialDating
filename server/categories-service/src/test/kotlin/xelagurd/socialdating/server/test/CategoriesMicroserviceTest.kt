package xelagurd.socialdating.server.test

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.testcontainers.containers.PostgreSQLContainer
import xelagurd.socialdating.server.FakeCategoriesData
import xelagurd.socialdating.server.FakeCategoriesData.filterByIds
import xelagurd.socialdating.server.FakeCategoriesData.filterByUserId
import xelagurd.socialdating.server.FakeCategoriesData.toUserCategoriesWithNullIds
import xelagurd.socialdating.server.model.Category
import xelagurd.socialdating.server.model.UserCategory
import xelagurd.socialdating.server.repository.UserCategoriesRepository
import xelagurd.socialdating.server.utils.TestUtils.readArrayFromJsonString
import xelagurd.socialdating.server.utils.TestUtils.readObject
import xelagurd.socialdating.server.utils.TestUtils.readObjectFromJsonString
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@ActiveProfiles("dev", "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoSecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CategoriesMicroserviceTest(
    @param:Autowired val restTemplate: TestRestTemplate,
    @param:Autowired val userCategoriesRepository: UserCategoriesRepository
) {

    private val currentUserId = 1
    private val anotherUserId = 2
    private val categoryIds = listOf(1, 2)

    private val categoriesDetails = FakeCategoriesData.categoriesDetails
    private val categories = FakeCategoriesData.categories.take(categoriesDetails.size)
    private val userCategories = FakeCategoriesData.userCategories

    @BeforeAll
    fun addUserCategories() {
        userCategoriesRepository.saveAll(userCategories.toUserCategoriesWithNullIds())
    }

    @Order(1)
    @Test
    fun addCategory_validData_created() {
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
    fun getCategories_existData_ok() {
        val response = restTemplate.getForEntity(
            "/categories",
            Array<Category>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size, response.body!!.size)
        assertContentEquals(categories.toTypedArray(), response.body!!)
    }

    @Test
    fun getCategories_withIds_ok() {
        val expected = categories.filterByIds(categoryIds)
        val response = restTemplate.getForEntity(
            "/categories?categoryIds=${categoryIds.toRequestParams()}",
            Array<Category>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected.size, response.body!!.size)
        assertContentEquals(expected.toTypedArray(), response.body!!)
    }

    @Test
    fun getUserCategories_existData_ok() {
        // maintained and notMaintained lists are not serialized, so they are not expected in the response
        val expected = userCategories.filterByUserId(currentUserId)
            .map { UserCategory(id = it.id, interest = it.interest, userId = it.userId, categoryId = it.categoryId) }
        val response = restTemplate.getForEntity(
            "/categories/users?userId=$currentUserId",
            Array<UserCategory>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected.size, response.body!!.size)
        assertContentEquals(expected.toTypedArray(), response.body!!)
    }

    @Test
    fun getSimilarUsers_existData_returnsOnlySimilarUsers() {
        val response = restTemplate.getWithAuth(
            currentUserId,
            "/categories/users/similar-users?currentUserId=$currentUserId",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        // user 3 is as often opposite to user 1 as similar, so only user 2 is returned
        val responseSimilarUsers = readArrayFromJsonString(response.body!!)
        assertEquals(1, responseSimilarUsers.size)

        val responseSimilarUser = responseSimilarUsers.single()
        assertEquals(anotherUserId, responseSimilarUser["id"])
        assertEquals(3, responseSimilarUser["similarNumber"])
        assertEquals(1, responseSimilarUser["oppositeNumber"])
        assertEquals(
            listOf("RemoteCategory1" to 2, "RemoteCategory3" to 1),
            responseSimilarUser.readCategoriesWithDifference("similarCategories")
        )
        assertEquals(
            listOf("RemoteCategory2" to -1),
            responseSimilarUser.readCategoriesWithDifference("oppositeCategories")
        )
    }

    @Test
    fun getDetailedSimilarUser_existData_returnsSimilarityWithDefiningThemes() {
        val response = restTemplate.getWithAuth(
            currentUserId,
            "/categories/users/detailed-similar-user?currentUserId=$currentUserId&anotherUserId=$anotherUserId",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseDetailedSimilarUser = readObjectFromJsonString(response.body!!)
        assertEquals(3, responseDetailedSimilarUser["similarNumber"])
        assertEquals(1, responseDetailedSimilarUser["oppositeNumber"])

        val responseCategories = responseDetailedSimilarUser.readObject("categories")
        assertEquals(setOf("1", "2", "3"), responseCategories.keys)

        // defining themes are identified by their number in category, not by their id
        val responseCategory1 = responseCategories.readObject("1")
        assertEquals("SIMILAR", responseCategory1["similarityType"])
        assertEquals(2, responseCategory1["similarNumber"])
        assertEquals(0, responseCategory1["oppositeNumber"])
        assertEquals(mapOf("1" to "SIMILAR", "2" to "SIMILAR"), responseCategory1.readDefiningThemesSimilarity())

        val responseCategory2 = responseCategories.readObject("2")
        assertEquals("OPPOSITE", responseCategory2["similarityType"])
        assertEquals(0, responseCategory2["similarNumber"])
        assertEquals(1, responseCategory2["oppositeNumber"])
        assertEquals(mapOf("3" to "OPPOSITE"), responseCategory2.readDefiningThemesSimilarity())

        val responseCategory3 = responseCategories.readObject("3")
        assertEquals("SIMILAR", responseCategory3["similarityType"])
        assertEquals(1, responseCategory3["similarNumber"])
        assertEquals(0, responseCategory3["oppositeNumber"])
        assertEquals(mapOf("1" to "SIMILAR"), responseCategory3.readDefiningThemesSimilarity())
    }

    private fun Map<String, Any>.readCategoriesWithDifference(key: String) =
        (this[key] as List<*>)
            .map { it as Map<*, *> }
            .map { it["name"] to it["differenceNumber"] }

    private fun Map<String, Any>.readDefiningThemesSimilarity() =
        readObject("definingThemes")
            .mapValues { (_, definingTheme) -> (definingTheme as Map<*, *>)["similarityType"] }

    private fun <T> TestRestTemplate.getWithAuth(
        userId: Int,
        url: String,
        responseType: Class<T>
    ): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.set("X-Auth-UserId", userId.toString())
        headers.set("X-Auth-Role", "USER")
        return exchange(url, HttpMethod.GET, HttpEntity<Void>(headers), responseType)
    }

    companion object {
        @ServiceConnection
        val postgresContainer = PostgreSQLContainer("postgres:18")
            .apply {
                withDatabaseName("test_db")
                withUsername("test_user")
                withPassword("test_password")
                withInitScript("init-schema.sql")
            }
    }
}
