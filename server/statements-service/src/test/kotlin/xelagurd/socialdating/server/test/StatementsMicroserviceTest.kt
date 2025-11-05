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
import xelagurd.socialdating.server.FakeStatementsData
import xelagurd.socialdating.server.FakeStatementsData.filterByUserIdAndDefiningThemeIds
import xelagurd.socialdating.server.FakeStatementsData.findUnreacted
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@ActiveProfiles("dev", "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoSecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatementsMicroserviceTest(@param:Autowired val restTemplate: TestRestTemplate) {

    private val userId = 1
    private val definingThemeIds = listOf(1, 3)

    private val statementsDetails = FakeStatementsData.statementsDetails
    private val statements = FakeStatementsData.statements.take(statementsDetails.size)
    private val userStatementsDetails = FakeStatementsData.userStatementsDetails
    private val userStatements = FakeStatementsData.userStatements.take(userStatementsDetails.size)

    init {
        addStatements()
        addUserStatements()
    }

    @Test
    fun testGetStatements() {
        getStatements()
    }

    @Test
    fun testGetUserStatements() {
        getUserStatements()
    }

    private fun addStatements() {
        statementsDetails.forEachIndexed { index, statementDetails ->
            val response = restTemplate.postForEntity(
                "/api/v1/statements",
                statementDetails,
                Statement::class.java
            )
            assertEquals(HttpStatus.CREATED, response.statusCode)
            assertEquals(statements[index], response.body!!)
        }
    }

    private fun addUserStatements() {
        userStatementsDetails.forEachIndexed { index, userStatementDetails ->
            val response = restTemplate.postForEntity(
                "/api/v1/statements/users",
                userStatementDetails,
                UserStatement::class.java
            )
            assertEquals(HttpStatus.CREATED, response.statusCode)
            assertEquals(userStatements[index], response.body!!)
        }
    }

    private fun getStatements() {
        val expected = statements.findUnreacted(userId, definingThemeIds, userStatements)
        val response = restTemplate.getForEntity(
            "/api/v1/statements?userId=${userId}&definingThemeIds=${definingThemeIds.toRequestParams()}",
            Array<Statement>::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(expected.size, response.body!!.size)
        assertContentEquals(expected.toTypedArray(), response.body!!)
    }

    private fun getUserStatements() {
        val expected = userStatements.filterByUserIdAndDefiningThemeIds(userId, definingThemeIds, statements)
        val response = restTemplate.getForEntity(
            "/api/v1/statements/users?userId=$userId&definingThemeIds=${definingThemeIds.toRequestParams()}",
            Array<UserStatement>::class.java
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