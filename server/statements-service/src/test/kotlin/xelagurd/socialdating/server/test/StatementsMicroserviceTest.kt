package xelagurd.socialdating.server.test

import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Import
import org.springframework.core.ParameterizedTypeReference
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
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.details.DefiningThemeReactionDetails
import xelagurd.socialdating.server.model.dto.PageDto
import xelagurd.socialdating.server.model.dto.StatementDto
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.repository.UserStatementsRepository
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoSecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class StatementsMicroserviceTest(
    @param:Autowired val restTemplate: TestRestTemplate,
    @param:Autowired val userStatementsRepository: UserStatementsRepository
) {

    private val currentUserId = Random.nextInt(1, Int.MAX_VALUE)

    private val pagingUserId = Random.nextInt(1, Int.MAX_VALUE)
    private val orderUserId = Random.nextInt(1, Int.MAX_VALUE)

    private val definingThemeIds = listOf(1, 2)

    private val statementsDetails = FakeStatementsData.statementsDetails
    private val statementDtos = FakeStatementsData.statementDtos.take(statementsDetails.size)
    private val statementIds = statementDtos.map { it.id }

    private val reactedStatementIds = listOf(3, 4)
    private val unreactedStatements = statementDtos.filterNot { it.id in reactedStatementIds }

    private val pageSize = 2

    @BeforeAll
    fun addUserStatements() {
        reactStatements(currentUserId, reactedStatementIds)
    }

    @Order(1)
    @Test
    fun addStatement_validData_created() {
        statementsDetails.forEachIndexed { index, statementDetails ->
            val response = restTemplate.postForEntity(
                "/statements",
                statementDetails,
                StatementDto::class.java
            )
            assertEquals(HttpStatus.CREATED, response.statusCode)
            assertEquals(statementDtos[index], response.body!!)
        }
    }

    @Order(2)
    @Test
    fun addStatement_duplicatedDefiningThemes_badRequest() {
        val statementDetails = statementsDetails[0].copy(
            text = "RemoteStatementWithDuplicatedDefiningThemes",
            definingThemes = listOf(
                DefiningThemeReactionDetails(1, true),
                DefiningThemeReactionDetails(1, false)
            )
        )

        val response = restTemplate.postForEntity("/statements", statementDetails, String::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Order(3)
    @Test
    fun getStatements_withReactedStatements_returnsOnlyUnreactedWithoutDuplicates() {
        val response = getStatements(currentUserId)

        assertEquals(HttpStatus.OK, response.statusCode)
        with(response.body!!.content) {
            assertEquals(unreactedStatements.size, size)
            assertEquals(unreactedStatements.toSet(), toSet())
        }
    }

    @Order(4)
    @Test
    fun getStatements_pagedToTheEnd_returnsEveryUnreactedStatementOnce() {
        val pagedStatements = mutableListOf<StatementDto>()
        var cursor: String? = null

        do {
            val response = getStatements(currentUserId, cursor, pageSize)
            assertEquals(HttpStatus.OK, response.statusCode)

            val page = response.body!!
            assertTrue(page.content.size <= pageSize)

            pagedStatements += page.content
            cursor = page.nextCursor
        } while (cursor != null)

        assertEquals(unreactedStatements.size, pagedStatements.size)
        assertEquals(unreactedStatements.toSet(), pagedStatements.toSet())
    }

    @Order(5)
    @Test
    fun getStatements_reactionsBetweenPages_skipsNothing() {
        val pagedStatements = mutableListOf<StatementDto>()
        var cursor: String? = null

        do {
            val response = getStatements(pagingUserId, cursor, pageSize)
            assertEquals(HttpStatus.OK, response.statusCode)

            val page = response.body!!
            pagedStatements += page.content
            cursor = page.nextCursor

            reactStatements(pagingUserId, page.content.map { it.id })
        } while (cursor != null)

        assertEquals(statementIds.size, pagedStatements.size)
        assertEquals(statementIds.toSet(), pagedStatements.map { it.id }.toSet())
        assertEquals(HttpStatus.NO_CONTENT, getStatements(pagingUserId).statusCode)
    }

    @Order(6)
    @Test
    fun getStatements_withoutCursor_returnsAnotherOrderEveryTime() {
        val orders = List(ORDER_ATTEMPTS_NUMBER) {
            getStatements(orderUserId).body!!.content.map { statement -> statement.id }
        }

        assertEquals(statementIds.size, orders.first().size)
        assertTrue(orders.toSet().size > 1)
    }

    @Order(7)
    @Test
    fun getStatements_sameCursor_returnsSamePage() {
        val firstPage = getStatements(orderUserId, size = pageSize).body!!
        val cursor = assertNotNull(firstPage.nextCursor)

        val secondPage = getStatements(orderUserId, cursor, pageSize).body!!
        val repeatedSecondPage = getStatements(orderUserId, cursor, pageSize).body!!

        assertEquals(secondPage, repeatedSecondPage)
        assertTrue(secondPage.content.none { it in firstPage.content })
    }

    @Order(8)
    @Test
    fun getStatements_lastPage_returnsNoNextCursor() {
        val response = getStatements(orderUserId, size = statementIds.size + 1)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNull(response.body!!.nextCursor)
    }

    @Order(9)
    @Test
    fun getStatements_wrongCursor_badRequest() {
        val response = restTemplate.exchange(
            statementsUrl(orderUserId, "wrongCursor", null),
            HttpMethod.GET,
            HttpEntity<Void>(authHeaders(orderUserId)),
            String::class.java
        )

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    private fun reactStatements(userId: Int, reactedIds: List<Int>) {
        userStatementsRepository.saveAll(
            reactedIds.map { UserStatement(reactionType = FULL_MAINTAIN, userId = userId, statementId = it) }
        )
    }

    private fun getStatements(
        userId: Int,
        cursor: String? = null,
        size: Int? = null
    ): ResponseEntity<PageDto<StatementDto>> =
        restTemplate.exchange(
            statementsUrl(userId, cursor, size),
            HttpMethod.GET,
            HttpEntity<Void>(authHeaders(userId)),
            object : ParameterizedTypeReference<PageDto<StatementDto>>() {}
        )

    private fun statementsUrl(userId: Int, cursor: String?, size: Int?) =
        "/statements?currentUserId=$userId&definingThemeIds=${definingThemeIds.toRequestParams()}" +
                (cursor?.let { "&cursor=$it" } ?: "") +
                (size?.let { "&size=$it" } ?: "")

    private fun authHeaders(userId: Int) =
        HttpHeaders().apply {
            set("X-Auth-UserId", userId.toString())
            set("X-Auth-Role", "USER")
        }

    companion object {
        private const val ORDER_ATTEMPTS_NUMBER = 10

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
