package xelagurd.socialdating.server.integrationTest

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.utils.TestUtils.readArrayFromJsonString
import xelagurd.socialdating.server.utils.TestUtils.readObjectFromJsonString

class StatementsIntegrationTest {
    private val restTemplate = RestTemplate()
    private val GATEWAY_URL = "http://localhost:8080"

    @Test
    fun addStatementReaction() {
        val userId = 1
        var userCategoryId = -1

        val requestCategory = mapOf(
            "name" to "TestRemoteCategory1"
        )
        val postCategoryResponse = restTemplate.postForEntity(
            "$GATEWAY_URL/api/v1/categories",
            requestCategory,
            String::class.java
        )
        assertThat(postCategoryResponse.statusCode).isEqualTo(HttpStatus.OK)

        val responseCategory = readObjectFromJsonString(postCategoryResponse.body!!)
        assertNotNull(responseCategory["id"])
        assertEquals(requestCategory["name"], responseCategory["name"])


        val requestDefiningTheme = mapOf(
            "name" to "TestRemoteDefiningTheme1",
            "fromOpinion" to "No",
            "toOpinion" to "Yes",
            "categoryId" to responseCategory["id"]
        )
        val postDefiningThemeResponse = restTemplate.postForEntity(
            "$GATEWAY_URL/api/v1/defining-themes",
            requestDefiningTheme,
            String::class.java
        )
        assertThat(postDefiningThemeResponse.statusCode).isEqualTo(HttpStatus.OK)

        val responseDefiningTheme = readObjectFromJsonString(postDefiningThemeResponse.body!!)
        assertNotNull(responseDefiningTheme["id"])
        assertEquals(requestDefiningTheme["name"], responseDefiningTheme["name"])
        assertEquals(requestDefiningTheme["fromOpinion"], responseDefiningTheme["fromOpinion"])
        assertEquals(requestDefiningTheme["toOpinion"], responseDefiningTheme["toOpinion"])
        assertEquals(requestDefiningTheme["categoryId"], responseDefiningTheme["categoryId"])


        val requestStatement = mapOf(
            "text" to "TestRemoteStatement1",
            "isSupportDefiningTheme" to true,
            "definingThemeId" to responseDefiningTheme["id"],
            "creatorUserId" to userId
        )
        val postStatementResponse = restTemplate.postForEntity(
            "$GATEWAY_URL/api/v1/statements",
            requestStatement,
            String::class.java
        )
        assertThat(postStatementResponse.statusCode).isEqualTo(HttpStatus.OK)

        val responseStatement = readObjectFromJsonString(postStatementResponse.body!!)
        assertNotNull(responseStatement["id"])
        assertEquals(requestStatement["text"], responseStatement["text"])
        assertEquals(requestStatement["isSupportDefiningTheme"], responseStatement["isSupportDefiningTheme"])
        assertEquals(requestStatement["definingThemeId"], responseStatement["definingThemeId"])
        assertEquals(requestStatement["creatorUserId"], responseStatement["creatorUserId"])


        val requestStatementReaction = mapOf(
            "userOrUserCategoryId" to userId,
            "categoryId" to responseCategory["id"],
            "definingThemeId" to responseDefiningTheme["id"],
            "reactionType" to 4, // FULL_MAINTAIN
            "isSupportDefiningTheme" to true
        )
        val postStatementReactionResponse = restTemplate.postForEntity(
            "$GATEWAY_URL/api/v1/statements/${responseStatement["id"]}/reaction",
            requestStatementReaction,
            String::class.java
        )
        assertThat(postStatementReactionResponse.statusCode).isEqualTo(HttpStatus.OK)


        Thread.sleep(3000)


        val getUserCategoriesResponse = restTemplate.getForEntity(
            "$GATEWAY_URL/api/v1/categories/users/$userId",
            String::class.java
        )
        assertThat(getUserCategoriesResponse.statusCode).isEqualTo(HttpStatus.OK)

        val responseUserCategories = readArrayFromJsonString(getUserCategoriesResponse.body!!)
        var containNewUserCategory = false
        for (userCategory in responseUserCategories) {
            if (userCategory["categoryId"] == responseCategory["id"]) {
                containNewUserCategory = true
                userCategoryId = userCategory["id"] as Int
                break
            }
        }
        assertTrue(containNewUserCategory)


        val getUserDefiningThemesResponse = restTemplate.getForEntity(
            "$GATEWAY_URL/api/v1/defining-themes/users?userCategoryIds=$userCategoryId",
            String::class.java
        )
        assertThat(getUserDefiningThemesResponse.statusCode).isEqualTo(HttpStatus.OK)

        val responseUserDefiningThemes = readArrayFromJsonString(getUserDefiningThemesResponse.body!!)
        var containNewUserDefiningTheme = false
        for (userDefiningTheme in responseUserDefiningThemes) {
            if (userDefiningTheme["definingThemeId"] == responseDefiningTheme["id"]) {
                containNewUserDefiningTheme = true
                break
            }
        }
        assertTrue(containNewUserDefiningTheme)
    }
}