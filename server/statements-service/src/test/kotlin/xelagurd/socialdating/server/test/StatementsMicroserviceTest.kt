package xelagurd.socialdating.server.test

import kotlin.random.Random
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
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.UserStatement
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

    // any user without reactions is suitable, all the needed reactions are added by this test
    private val currentUserId = Random.nextInt(1, Int.MAX_VALUE)
    private val definingThemeIds = listOf(1, 2)
    private val reactedDefiningThemeId = 2

    private val statementsDetails = FakeStatementsData.statementsDetails
    private val statements = FakeStatementsData.statements.take(statementsDetails.size)
    private val reactedStatements = statements.filter { it.definingThemeId == reactedDefiningThemeId }
    private val unreactedStatements = statements - reactedStatements.toSet()

    @BeforeAll
    fun addUserStatements() {
        userStatementsRepository.saveAll(
            reactedStatements.map {
                UserStatement(reactionType = FULL_MAINTAIN, userId = currentUserId, statementId = it.id!!)
            }
        )
    }

    @Order(1)
    @Test
    fun addStatement_validData_created() {
        statementsDetails.forEachIndexed { index, statementDetails ->
            val response = restTemplate.postForEntity(
                "/statements",
                statementDetails,
                Statement::class.java
            )
            assertEquals(HttpStatus.CREATED, response.statusCode)
            assertEquals(statements[index], response.body!!)
        }
    }

    @Test
    fun getStatements_withReactedStatements_returnsOnlyUnreacted() {
        val response = restTemplate.getWithAuth(
            currentUserId,
            "/statements?currentUserId=$currentUserId&definingThemeIds=${definingThemeIds.toRequestParams()}",
            Array<Statement>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(unreactedStatements.size, response.body!!.size)
        assertContentEquals(unreactedStatements.toTypedArray(), response.body!!)
    }

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
