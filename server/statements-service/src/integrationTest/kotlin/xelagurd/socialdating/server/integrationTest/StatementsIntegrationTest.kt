package xelagurd.socialdating.server.integrationTest

import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.junit.jupiter.api.Test
import xelagurd.socialdating.server.utils.TestUtils.readArrayFromJsonString
import xelagurd.socialdating.server.utils.TestUtils.readObjectFromJsonString

class StatementsIntegrationTest {
    private val restTemplate = RestTemplate()
    private val GATEWAY_URL = "http://localhost:8080"
    private lateinit var accessToken: String

    private fun <T> getWithToken(url: String, responseType: Class<T>): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val entity = HttpEntity<Void>(headers)
        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType)
    }

    private fun <T> postWithToken(url: String, body: Any, responseType: Class<T>): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(accessToken)
        val entity = HttpEntity(body, headers)
        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType)
    }

    @Test
    fun addStatementReaction() {
        val loginRequest = mapOf(
            "username" to "username1",
            "password" to "password1"
        )
        val loginResponse = restTemplate.postForEntity(
            "$GATEWAY_URL/api/v1/users/auth/login",
            loginRequest,
            String::class.java
        )
        assertEquals(loginResponse.statusCode, HttpStatus.OK)

        val responseAuth = readObjectFromJsonString(loginResponse.body!!)
        assertNotNull(responseAuth["accessToken"])
        accessToken = responseAuth["accessToken"] as String
        assertNotNull(responseAuth["user"])
        val user = responseAuth["user"] as HashMap<*, *>
        assertNotNull(user["id"])
        val userId = user["id"]

        val requestCategory = mapOf(
            "name" to ("TestRemoteCategory" + Random.nextLong())
        )
        val postCategoryResponse = postWithToken(
            "$GATEWAY_URL/api/v1/categories",
            requestCategory,
            String::class.java
        )
        assertEquals(postCategoryResponse.statusCode, HttpStatus.CREATED)

        val responseCategory = readObjectFromJsonString(postCategoryResponse.body!!)
        assertNotNull(responseCategory["id"])
        assertEquals(requestCategory["name"], responseCategory["name"])


        val requestDefiningTheme = mapOf(
            "name" to ("TestRemoteDefiningTheme" + Random.nextLong()),
            "fromOpinion" to "No",
            "toOpinion" to "Yes",
            "categoryId" to responseCategory["id"]
        )
        val postDefiningThemeResponse = postWithToken(
            "$GATEWAY_URL/api/v1/defining-themes",
            requestDefiningTheme,
            String::class.java
        )
        assertEquals(postDefiningThemeResponse.statusCode, HttpStatus.CREATED)

        val responseDefiningTheme = readObjectFromJsonString(postDefiningThemeResponse.body!!)
        assertNotNull(responseDefiningTheme["id"])
        assertEquals(requestDefiningTheme["name"], responseDefiningTheme["name"])
        assertEquals(requestDefiningTheme["fromOpinion"], responseDefiningTheme["fromOpinion"])
        assertEquals(requestDefiningTheme["toOpinion"], responseDefiningTheme["toOpinion"])
        assertEquals(requestDefiningTheme["categoryId"], responseDefiningTheme["categoryId"])


        val requestStatement = mapOf(
            "text" to ("TestRemoteStatemen1" + Random.nextLong()),
            "isSupportDefiningTheme" to true,
            "definingThemeId" to responseDefiningTheme["id"],
            "creatorUserId" to userId
        )
        val postStatementResponse = postWithToken(
            "$GATEWAY_URL/api/v1/statements",
            requestStatement,
            String::class.java
        )
        assertEquals(postStatementResponse.statusCode, HttpStatus.CREATED)

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
            "statementId" to responseStatement["id"],
            "reactionType" to 4, // FULL_MAINTAIN
            "isSupportDefiningTheme" to true
        )
        val postStatementReactionResponse = postWithToken(
            "$GATEWAY_URL/api/v1/statements/users/reaction",
            requestStatementReaction,
            String::class.java
        )
        assertEquals(postStatementReactionResponse.statusCode, HttpStatus.CREATED)

        val responseUserStatement = readObjectFromJsonString(postStatementReactionResponse.body!!)
        assertNotNull(responseUserStatement["id"])
        assertEquals(requestStatementReaction["userOrUserCategoryId"], responseUserStatement["userId"])
        assertEquals(requestStatementReaction["statementId"], responseUserStatement["statementId"])


        Thread.sleep(3000)


        val getUserCategoriesResponse = getWithToken(
            "$GATEWAY_URL/api/v1/categories/users?userId=$userId",
            String::class.java
        )
        assertEquals(getUserCategoriesResponse.statusCode, HttpStatus.OK)

        val responseUserCategories = readArrayFromJsonString(getUserCategoriesResponse.body!!)
        val newUserCategory =
            responseUserCategories.firstOrNull { it["categoryId"] == responseCategory["id"] }
        assertNotNull(newUserCategory)
        val userCategoryId = newUserCategory["id"] as Int


        val getUserDefiningThemesResponse = getWithToken(
            "$GATEWAY_URL/api/v1/defining-themes/users?userCategoryIds=$userCategoryId",
            String::class.java
        )
        assertEquals(getUserDefiningThemesResponse.statusCode, HttpStatus.OK)

        val responseUserDefiningThemes = readArrayFromJsonString(getUserDefiningThemesResponse.body!!)
        val newUserDefiningTheme =
            responseUserDefiningThemes.firstOrNull { it["definingThemeId"] == responseDefiningTheme["id"] }
        assertNotNull(newUserDefiningTheme)
    }
}