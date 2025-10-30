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
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.UserStatement
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.model.details.UserStatementDetails
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(NoSecurityConfig::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatementsMicroserviceTest(@param:Autowired val restTemplate: TestRestTemplate) {

    private val userId = 1
    private val definingThemeIds = listOf(1, 3)

    private val statements = listOf(
        Statement(
            id = 1,
            text = "RemoteStatement1",
            isSupportDefiningTheme = true,
            definingThemeId = 1,
            creatorUserId = userId
        ),
        Statement(
            id = 2,
            text = "RemoteStatement2",
            isSupportDefiningTheme = true,
            definingThemeId = 2,
            creatorUserId = userId
        ),
        Statement(
            id = 3,
            text = "RemoteStatement3",
            isSupportDefiningTheme = true,
            definingThemeId = 3,
            creatorUserId = userId
        )
    )

    private val statementsDetails = listOf(
        StatementDetails(
            text = "RemoteStatement1", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = userId
        ),
        StatementDetails(
            text = "RemoteStatement2", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = userId
        ),
        StatementDetails(
            text = "RemoteStatement3", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = userId
        )
    )

    private val userStatements = listOf(
        UserStatement(id = 1, reactionType = FULL_MAINTAIN, userId = 1, statementId = 1),
        UserStatement(id = 2, reactionType = FULL_MAINTAIN, userId = 1, statementId = 2),
        UserStatement(id = 3, reactionType = FULL_MAINTAIN, userId = 2, statementId = 3)
    )
    private val userStatementsDetails = listOf(
        UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 1, statementId = 1),
        UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 1, statementId = 2),
        UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 2, statementId = 3)
    )

    init {
        val postResponse1 = restTemplate.postForEntity(
            "/api/v1/statements",
            statementsDetails[0],
            Statement::class.java
        )
        assertThat(postResponse1.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse1.body!!, statements[0])

        val postResponse2 = restTemplate.postForEntity(
            "/api/v1/statements",
            statementsDetails[1],
            Statement::class.java
        )
        assertThat(postResponse2.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse2.body!!, statements[1])

        val postResponse3 = restTemplate.postForEntity(
            "/api/v1/statements",
            statementsDetails[2],
            Statement::class.java
        )
        assertThat(postResponse3.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse3.body!!, statements[2])

        val postResponse4 = restTemplate.postForEntity(
            "/api/v1/statements/users",
            userStatementsDetails[0],
            UserStatement::class.java
        )
        assertThat(postResponse4.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse4.body!!, userStatements[0])

        val postResponse5 = restTemplate.postForEntity(
            "/api/v1/statements/users",
            userStatementsDetails[1],
            UserStatement::class.java
        )
        assertThat(postResponse5.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse5.body!!, userStatements[1])

        val postResponse6 = restTemplate.postForEntity(
            "/api/v1/statements/users",
            userStatementsDetails[2],
            UserStatement::class.java
        )
        assertThat(postResponse6.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse6.body!!, userStatements[2])
    }

    @Test
    fun getStatements() {
        val getResponse2 = restTemplate.getForEntity(
            "/api/v1/statements?userId=${userId}&definingThemeIds=${definingThemeIds.toRequestParams()}",
            Array<Statement>::class.java
        )
        assertThat(getResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse2.body!!.size, 1)
        assertContentEquals(getResponse2.body!!, arrayOf(statements[2]))
    }

    @Test
    fun addStatement_notUniqueName_error() {
        val statementDetails = StatementDetails(
            text = "RemoteStatement1",
            isSupportDefiningTheme = true,
            definingThemeId = 1,
            creatorUserId = 1
        )
        val postResponse = restTemplate.postForEntity(
            "/api/v1/statements",
            statementDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.CONFLICT)
        assertEquals(postResponse.body!!, "Statement with 'RemoteStatement1' text already exists")
    }

    @Test
    fun addStatement_emptyName_error() {
        val statementDetails = StatementDetails(
            text = "",
            isSupportDefiningTheme = true,
            definingThemeId = 1,
            creatorUserId = 1
        )
        val postResponse = restTemplate.postForEntity(
            "/api/v1/statements",
            statementDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertEquals(postResponse.body!!, "'Text' must not be blank")
    }

    @Test
    fun addStatement_wrongDefiningThemeId_error() {
        val statementDetails = StatementDetails(
            text = "RemoteStatement4",
            isSupportDefiningTheme = true,
            definingThemeId = -5,
            creatorUserId = 1
        )
        val postResponse = restTemplate.postForEntity(
            "/api/v1/statements",
            statementDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertEquals(postResponse.body!!, "'DefiningThemeId' must be greater than or equal to 1")
    }

    @Test
    fun addStatement_emptyNameAndWrongDefiningThemeId_error() {
        val statementDetails = StatementDetails(
            text = "",
            isSupportDefiningTheme = true,
            definingThemeId = -5,
            creatorUserId = 1
        )
        val postResponse = restTemplate.postForEntity(
            "/api/v1/statements",
            statementDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertEquals(
            postResponse.body!!,
            "'DefiningThemeId' must be greater than or equal to 1; 'Text' must not be blank"
        )
    }

    @Test
    fun getUserStatements() {
        val getResponse = restTemplate.getForEntity("/api/v1/statements/users?userId=$userId&definingThemeIds=${definingThemeIds.toRequestParams()}", Array<UserStatement>::class.java)
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse.body!!.size, 1)
        assertContentEquals(getResponse.body!!, arrayOf(userStatements[0]))
    }

    @Test
    fun addUserStatement_wrongStatementId_error() {
        val userStatementDetails = UserStatementDetails(reactionType = FULL_MAINTAIN, userId = 1, statementId = -5)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/statements/users",
            userStatementDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertEquals(postResponse.body!!, "'StatementId' must be greater than or equal to 1")
    }
}