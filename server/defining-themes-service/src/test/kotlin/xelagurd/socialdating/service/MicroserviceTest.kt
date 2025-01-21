package xelagurd.socialdating.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import xelagurd.socialdating.dto.DefiningTheme
import xelagurd.socialdating.dto.DefiningThemeDetails
import xelagurd.socialdating.service.TestUtils.toRequestParams
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MicroserviceTest(@Autowired val restTemplate: TestRestTemplate) {

    private val categoryId = 1
    private val categoryIds = listOf(1, 3)

    private val definingThemes = listOf(
        DefiningTheme(id = 1, name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningTheme(id = 2, name = "RemoteDefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningTheme(id = 3, name = "RemoteDefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 3)
    )
    private val definingThemesDetails = listOf(
        DefiningThemeDetails(name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningThemeDetails(name = "RemoteDefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningThemeDetails(name = "RemoteDefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 3)
    )

    @Test
    fun addDefiningThemesAndGetThem() {
        val postResponse1 = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemesDetails[0],
            DefiningTheme::class.java
        )
        assertThat(postResponse1.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse1.body!!, definingThemes[0])

        val postResponse2 = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemesDetails[1],
            DefiningTheme::class.java
        )
        assertThat(postResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse2.body!!, definingThemes[1])

        val postResponse3 = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemesDetails[2],
            DefiningTheme::class.java
        )
        assertThat(postResponse3.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse3.body!!, definingThemes[2])

        val getResponse1 = restTemplate.getForEntity(
            "/api/v1/defining-themes?categoryIds=$categoryId",
            Array<DefiningTheme>::class.java
        )
        assertThat(getResponse1.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse1.body!!.size, 1)
        assertContentEquals(getResponse1.body!!, arrayOf(definingThemes[0]))

        val getResponse2 = restTemplate.getForEntity(
            "/api/v1/defining-themes?categoryIds=${categoryIds.toRequestParams()}",
            Array<DefiningTheme>::class.java
        )
        assertThat(getResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse2.body!!.size, 2)
        assertContentEquals(getResponse2.body!!, arrayOf(definingThemes[0], definingThemes[2]))
    }
}