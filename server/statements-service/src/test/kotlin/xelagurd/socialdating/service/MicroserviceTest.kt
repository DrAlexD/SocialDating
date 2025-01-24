package xelagurd.socialdating.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import xelagurd.socialdating.dto.Statement
import xelagurd.socialdating.dto.StatementDetails
import xelagurd.socialdating.service.TestUtils.toRequestParams
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MicroserviceTest(@Autowired val restTemplate: TestRestTemplate) {

    private val userId = 1
    private val definingThemeIds = listOf(1, 3)

    private val statements = listOf(
        Statement(
            id = 1, text = "RemoteStatement1", isSupportDefiningTheme = true, definingThemeId = 1, creatorUserId = userId
        ),
        Statement(
            id = 2, text = "RemoteStatement2", isSupportDefiningTheme = true, definingThemeId = 2, creatorUserId = userId
        ),
        Statement(
            id = 3, text = "RemoteStatement3", isSupportDefiningTheme = true, definingThemeId = 3, creatorUserId = userId
        )
    )

    private val statementsDetails = listOf(
        StatementDetails(
            text = "RemoteStatement1", isSupportDefiningTheme = true, definingThemeId = 1, userId = userId
        ),
        StatementDetails(
            text = "RemoteStatement2", isSupportDefiningTheme = true, definingThemeId = 2, userId = userId
        ),
        StatementDetails(
            text = "RemoteStatement3", isSupportDefiningTheme = true, definingThemeId = 3, userId = userId
        )
    )

    @Test
    fun addStatementsAndGetThem() {
        val postResponse1 = restTemplate.postForEntity(
            "/api/v1/statements",
            statementsDetails[0],
            Statement::class.java
        )
        assertThat(postResponse1.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse1.body!!, statements[0])

        val postResponse2 = restTemplate.postForEntity(
            "/api/v1/statements",
            statementsDetails[1],
            Statement::class.java
        )
        assertThat(postResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse2.body!!, statements[1])

        val postResponse3 = restTemplate.postForEntity(
            "/api/v1/statements",
            statementsDetails[2],
            Statement::class.java
        )
        assertThat(postResponse3.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse3.body!!, statements[2])

        val getResponse2 = restTemplate.getForEntity(
            "/api/v1/statements?definingThemeIds=${definingThemeIds.toRequestParams()}",
            Array<Statement>::class.java
        )
        assertThat(getResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse2.body!!.size, 2)
        assertContentEquals(getResponse2.body!!, arrayOf(statements[0], statements[2]))
    }
}