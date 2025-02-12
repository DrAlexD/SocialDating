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
import xelagurd.socialdating.server.model.DefiningTheme
import xelagurd.socialdating.server.model.UserDefiningTheme
import xelagurd.socialdating.server.model.details.DefiningThemeDetails
import xelagurd.socialdating.server.model.details.UserDefiningThemeDetails
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefiningThemesMicroserviceTest(@Autowired val restTemplate: TestRestTemplate) {

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

    private val userCategoryIds = listOf(1, 3)

    private val userDefiningThemes = listOf(
        UserDefiningTheme(id = 1, value = 10, interest = 10, userCategoryId = 1, definingThemeId = 1),
        UserDefiningTheme(id = 2, value = 15, interest = 15, userCategoryId = 2, definingThemeId = 2),
        UserDefiningTheme(id = 3, value = 20, interest = 20, userCategoryId = 3, definingThemeId = 3)
    )
    private val userDefiningThemesDetails = listOf(
        UserDefiningThemeDetails(value = 10, interest = 10, userCategoryId = 1, definingThemeId = 1),
        UserDefiningThemeDetails(value = 15, interest = 15, userCategoryId = 2, definingThemeId = 2),
        UserDefiningThemeDetails(value = 20, interest = 20, userCategoryId = 3, definingThemeId = 3)
    )

    init {
        val postResponse1 = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemesDetails[0],
            DefiningTheme::class.java
        )
        assertThat(postResponse1.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse1.body!!, definingThemes[0])

        val postResponse2 = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemesDetails[1],
            DefiningTheme::class.java
        )
        assertThat(postResponse2.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse2.body!!, definingThemes[1])

        val postResponse3 = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemesDetails[2],
            DefiningTheme::class.java
        )
        assertThat(postResponse3.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse3.body!!, definingThemes[2])

        val postResponse4 = restTemplate.postForEntity(
            "/api/v1/defining-themes/users",
            userDefiningThemesDetails[0],
            UserDefiningTheme::class.java
        )
        assertThat(postResponse4.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse4.body!!, userDefiningThemes[0])

        val postResponse5 = restTemplate.postForEntity(
            "/api/v1/defining-themes/users",
            userDefiningThemesDetails[1],
            UserDefiningTheme::class.java
        )
        assertThat(postResponse5.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse5.body!!, userDefiningThemes[1])

        val postResponse6 = restTemplate.postForEntity(
            "/api/v1/defining-themes/users",
            userDefiningThemesDetails[2],
            UserDefiningTheme::class.java
        )
        assertThat(postResponse6.statusCode).isEqualTo(HttpStatus.CREATED)
        assertEquals(postResponse6.body!!, userDefiningThemes[2])
    }

    @Test
    fun getDefiningThemes() {
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
    fun addDefiningTheme_notUniqueName_error() {
        val definingThemeDetails =
            DefiningThemeDetails(name = "RemoteDefiningTheme1", fromOpinion = "No", toOpinion = "Yes", categoryId = 1)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemeDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun addDefiningTheme_emptyName_error() {
        val definingThemeDetails =
            DefiningThemeDetails(name = "", fromOpinion = "No", toOpinion = "Yes", categoryId = 1)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemeDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun addDefiningTheme_wrongCategoryId_error() {
        val definingThemeDetails =
            DefiningThemeDetails(name = "RemoteDefiningTheme4", fromOpinion = "No", toOpinion = "Yes", categoryId = -5)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemeDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun addDefiningTheme_emptyNameAndWrongCategoryId_error() {
        val definingThemeDetails =
            DefiningThemeDetails(name = "", fromOpinion = "No", toOpinion = "Yes", categoryId = -5)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/defining-themes",
            definingThemeDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun getUserDefiningThemes() {
        val getResponse2 = restTemplate.getForEntity(
            "/api/v1/defining-themes/users?userCategoryIds=${userCategoryIds.toRequestParams()}",
            Array<UserDefiningTheme>::class.java
        )
        assertThat(getResponse2.statusCode).isEqualTo(HttpStatus.OK)
        assertEquals(getResponse2.body!!.size, 2)
        assertContentEquals(getResponse2.body!!, arrayOf(userDefiningThemes[0], userDefiningThemes[2]))
    }

    @Test
    fun addUserDefiningTheme_wrongValue_error() {
        val userDefiningThemeDetails =
            UserDefiningThemeDetails(value = -10, interest = 34, userCategoryId = 1, definingThemeId = 1)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/defining-themes/users",
            userDefiningThemeDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun addUserDefiningTheme_wrongDefiningThemeId_error() {
        val userDefiningThemeDetails =
            UserDefiningThemeDetails(value = 23, interest = 34, userCategoryId = 1, definingThemeId = -4)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/defining-themes/users",
            userDefiningThemeDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun addUserDefiningTheme_wrongValueAndDefiningThemeId_error() {
        val userDefiningThemeDetails =
            UserDefiningThemeDetails(value = -10, interest = 34, userCategoryId = 1, definingThemeId = -4)
        val postResponse = restTemplate.postForEntity(
            "/api/v1/defining-themes/users",
            userDefiningThemeDetails,
            String::class.java
        )
        assertThat(postResponse.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }
}