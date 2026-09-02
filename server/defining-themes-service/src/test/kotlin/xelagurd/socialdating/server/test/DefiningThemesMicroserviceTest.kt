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
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.testcontainers.containers.PostgreSQLContainer
import xelagurd.socialdating.server.FakeDefiningThemesData
import xelagurd.socialdating.server.FakeDefiningThemesData.filterByCategoryId
import xelagurd.socialdating.server.FakeDefiningThemesData.filterByIds
import xelagurd.socialdating.server.FakeDefiningThemesData.filterByUserId
import xelagurd.socialdating.server.FakeDefiningThemesData.toUserDefiningThemesWithNullIds
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.details.DefiningThemeOrderDetails
import xelagurd.socialdating.server.repository.UserDefiningThemesRepository
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoSecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class DefiningThemesMicroserviceTest(
    @param:Autowired val restTemplate: TestRestTemplate,
    @param:Autowired val userDefiningThemesRepository: UserDefiningThemesRepository
) {

    private val currentUserId = 1
    private val categoryId = 1
    private val anotherCategoryId = 2
    private val definingThemeIds = listOf(1, 2)

    private val definingThemesDetails = FakeDefiningThemesData.definingThemesDetails
    private val definingThemes = FakeDefiningThemesData.definingThemes.take(definingThemesDetails.size)
    private val userDefiningThemes = FakeDefiningThemesData.userDefiningThemes

    @BeforeAll
    fun addUserDefiningThemes() {
        userDefiningThemesRepository.saveAll(userDefiningThemes.toUserDefiningThemesWithNullIds())
    }

    @Order(1)
    @Test
    fun addDefiningTheme_validData_created() {
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

    @Test
    fun getDefiningThemes_withCategoryId_ok() {
        val expected = definingThemes.filterByCategoryId(categoryId)
        val response = restTemplate.getForEntity(
            "/defining-themes?categoryId=$categoryId",
            Array<DefiningTheme>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected.size, response.body!!.size)
        assertContentEquals(expected.toTypedArray(), response.body!!)
    }

    @Test
    fun getDefiningThemes_withIds_ok() {
        val expected = definingThemes.filterByIds(definingThemeIds)
        val response = restTemplate.getForEntity(
            "/defining-themes?definingThemeIds=${definingThemeIds.toRequestParams()}",
            Array<DefiningTheme>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected.size, response.body!!.size)
        assertContentEquals(expected.toTypedArray(), response.body!!)
    }

    @Test
    fun getDefiningThemes_existData_ok() {
        val expected = definingThemes.sortedWith(compareBy({ it.orderNumber }, { it.categoryId }))
        val response = restTemplate.getForEntity(
            "/defining-themes",
            Array<DefiningTheme>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected.size, response.body!!.size)
        assertContentEquals(expected.toTypedArray(), response.body!!)
    }

    @Test
    fun getUserDefiningThemes_existData_ok() {
        val expected = userDefiningThemes.filterByUserId(currentUserId)
        val response = restTemplate.getForEntity(
            "/defining-themes/users?userId=$currentUserId",
            Array<UserDefiningTheme>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected.size, response.body!!.size)
        assertContentEquals(expected.toTypedArray(), response.body!!)
    }

    @Test
    fun moveDefiningTheme_backwards_shiftsJumpedOverThemesForwardInsideCategory() {
        val movedDefiningThemeId = definingThemes.filterByCategoryId(categoryId).last().id!!

        moveDefiningTheme(movedDefiningThemeId, 1)
        // 1, 2, 3 -> 2, 3, 1
        assertEquals(listOf(2, 3, 1), getOrderNumbersByDefiningThemeId(categoryId))
        assertEquals(listOf(1, 2), getOrderNumbersByDefiningThemeId(anotherCategoryId))

        moveDefiningTheme(movedDefiningThemeId, 3)
        assertEquals(listOf(1, 2, 3), getOrderNumbersByDefiningThemeId(categoryId))
    }

    @Test
    fun moveDefiningTheme_forwards_shiftsJumpedOverThemesBackwardInsideCategory() {
        val movedDefiningThemeId = definingThemes.filterByCategoryId(categoryId).first().id!!

        moveDefiningTheme(movedDefiningThemeId, 3)
        // 1, 2, 3 -> 3, 1, 2
        assertEquals(listOf(3, 1, 2), getOrderNumbersByDefiningThemeId(categoryId))
        assertEquals(listOf(1, 2), getOrderNumbersByDefiningThemeId(anotherCategoryId))

        moveDefiningTheme(movedDefiningThemeId, 1)
        assertEquals(listOf(1, 2, 3), getOrderNumbersByDefiningThemeId(categoryId))
    }

    @Test
    fun moveDefiningTheme_orderNumberAboveCategoryMax_badRequest() {
        val definingThemesInCategory = definingThemes.filterByCategoryId(categoryId)
        val response = restTemplate.postForEntity(
            "/defining-themes/order",
            DefiningThemeOrderDetails(
                definingThemeId = definingThemesInCategory.first().id!!,
                orderNumber = definingThemesInCategory.size + 1
            ),
            String::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals(listOf(1, 2, 3), getOrderNumbersByDefiningThemeId(categoryId))
    }

    private fun moveDefiningTheme(definingThemeId: Int, orderNumber: Int) {
        val response = restTemplate.postForEntity(
            "/defining-themes/order",
            DefiningThemeOrderDetails(definingThemeId = definingThemeId, orderNumber = orderNumber),
            DefiningTheme::class.java
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(orderNumber, response.body!!.orderNumber)
    }

    private fun getOrderNumbersByDefiningThemeId(categoryId: Int): List<Int> {
        val response = restTemplate.getForEntity(
            "/defining-themes?categoryId=$categoryId",
            Array<DefiningTheme>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        return response.body!!.sortedBy { it.id }.map { it.orderNumber }
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
