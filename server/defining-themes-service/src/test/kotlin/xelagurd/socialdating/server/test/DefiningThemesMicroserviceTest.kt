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
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.FakeDefiningThemesData.filterByCategoryId
import xelagurd.socialdating.server.model.DefiningTheme

@ActiveProfiles("dev", "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoSecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DefiningThemesMicroserviceTest(@param:Autowired val restTemplate: TestRestTemplate) {

    private val categoryId = 1

    private val definingThemesDetails = FakeDefiningThemesData.definingThemesDetails
    private val definingThemes = FakeDefiningThemesData.definingThemes.take(definingThemesDetails.size)

    @Order(1)
    @Test
    fun testAddDefiningTheme() {
        addDefiningThemes()
    }

    @Test
    fun testGetDefiningThemesByCategoryId() {
        getDefiningThemesByCategoryId()
    }

    @Test
    fun testGetDefiningThemes() {
        getDefiningThemes()
    }

    private fun addDefiningThemes() {
        definingThemesDetails.forEachIndexed { index, definingThemeDetails ->
            val response = restTemplate.postForEntity(
                "/defining-themes",
                definingThemeDetails,
                DefiningTheme::class.java
            )
            assertEquals(HttpStatus.CREATED, response.statusCode)
            assertEquals(definingThemes[index], response.body!!)
        }
    }

    private fun getDefiningThemesByCategoryId() {
        val expected = definingThemes.filterByCategoryId(categoryId)
        val response = restTemplate.getForEntity(
            "/defining-themes?categoryId=$categoryId",
            Array<DefiningTheme>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected.size, response.body!!.size)
        assertContentEquals(expected.toTypedArray(), response.body!!)
    }

    private fun getDefiningThemes() {
        val response = restTemplate.getForEntity(
            "/defining-themes",
            Array<DefiningTheme>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(definingThemes.size, response.body!!.size)
        assertContentEquals(definingThemes.toTypedArray(), response.body!!)
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