package xelagurd.socialdating.server.test

import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import xelagurd.socialdating.server.model.Statement
import xelagurd.socialdating.server.model.details.StatementDetails
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    }

    @Test
    fun getStatements() {
        val getResponse2 = restTemplate.getForEntity(
            "/api/v1/statements?definingThemeIds=${definingThemeIds.toRequestParams()}",
            Array<Statement>::class.java
        )
        assertThat(getResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse2.body!!.size, 2)
        assertContentEquals(getResponse2.body!!, arrayOf(statements[0], statements[2]))
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
}