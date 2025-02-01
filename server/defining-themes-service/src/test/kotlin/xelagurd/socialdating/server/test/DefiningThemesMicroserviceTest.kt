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
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DefiningThemesMicroserviceTest(@Autowired val restTemplate: TestRestTemplate) {

    private val categoryId = 1
    private val categoryIds = listOf(1, 3)

    private val definingThemes = listOf(
        DefiningTheme(id = 1, name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningTheme(id = 2, name = "RemoteDefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningTheme(id = 3, name = "RemoteDefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 3)
    )
    private val newDefiningThemes = listOf(
        DefiningTheme(name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1),
        DefiningTheme(name = "RemoteDefiningTheme2", fromOpinion = "No", toOpinion = "Yes", categoryId = 2),
        DefiningTheme(name = "RemoteDefiningTheme3", fromOpinion = "No", toOpinion = "Yes", categoryId = 3)
    )

    private val userCategoryIds = listOf(1, 3)

    private val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 10, interest = 10, userCategoryId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 15, interest = 15, userCategoryId = 2, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 20, interest = 20, userCategoryId = 3, definingThemeId = 3)
    )
    private val newUserDefiningThemes = listOf(
        UserDefiningTheme(value = 10, interest = 10, userCategoryId = 1, definingThemeId = 1),
        UserDefiningTheme(value = 15, interest = 15, userCategoryId = 2, definingThemeId = 2),
        UserDefiningTheme(value = 20, interest = 20, userCategoryId = 3, definingThemeId = 3)
    )

    @Test
    fun addDefiningThemesAndGetThem() {
        val postResponse1 = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            newDefiningThemes[0],
            DefiningTheme::class.java
        )
        assertThat(postResponse1.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse1.body!!, definingThemes[0])

        val postResponse2 = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            newDefiningThemes[1],
            DefiningTheme::class.java
        )
        assertThat(postResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse2.body!!, definingThemes[1])

        val postResponse3 = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            newDefiningThemes[2],
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

    @Test
    fun addUserDefiningThemesAndGetThem() {
        val postResponse1 = restTemplate.postForEntity(
            "/api/v1/defining-themes/users",
            newUserDefiningThemes[0],
            UserDefiningTheme::class.java
        )
        assertThat(postResponse1.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse1.body!!, userDefiningThemes[0])

        val postResponse2 = restTemplate.postForEntity(
            "/api/v1/defining-themes/users",
            newUserDefiningThemes[1],
            UserDefiningTheme::class.java
        )
        assertThat(postResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse2.body!!, userDefiningThemes[1])

        val postResponse3 = restTemplate.postForEntity(
            "/api/v1/defining-themes/users",
            newUserDefiningThemes[2],
            UserDefiningTheme::class.java
        )
        assertThat(postResponse3.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(postResponse3.body!!, userDefiningThemes[2])

        val getResponse2 = restTemplate.getForEntity(
            "/api/v1/defining-themes/users?userCategoryIds=${userCategoryIds.toRequestParams()}",
            Array<UserDefiningTheme>::class.java
        )
        assertThat(getResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse2.body!!.size, 2)
        assertContentEquals(getResponse2.body!!, arrayOf(userDefiningThemes[0], userDefiningThemes[2]))
    }
}