package xelagurd.socialdating.server.integrationTest

import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import xelagurd.socialdating.server.model.DefaultDataProperties.GATEWAY_URL
import xelagurd.socialdating.server.utils.TestUtils.readArrayFromJsonString
import xelagurd.socialdating.server.utils.TestUtils.readObjectFromJsonString

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IntegrationTest {
    private val restTemplate = TestRestTemplate()

    val randomNumber = Random.nextLong(100, 1000)

    private var accessToken = ""
    private var refreshToken = ""

    private var userId = -1
    private var categoryId = -1
    private var definingThemeId = -1
    private var statementId = -1

    @Order(1)
    @Test
    fun initializeData() {
        val responseAuth = loginUser()
        userId = (responseAuth["user"] as HashMap<*, *>)["id"] as Int
        accessToken = responseAuth["accessToken"] as String
        refreshToken = responseAuth["refreshToken"] as String

        categoryId = addCategory()
        definingThemeId = addDefiningTheme()
        statementId = addStatement()
    }

    @Order(2)
    @Test
    fun testProcessStatementReaction() {
        processStatementReaction()

        Thread.sleep(5000)

        // TODO getStatements()

        getUserCategories()
        getUserDefiningThemes()
    }

    @Test
    fun testGetUser() {
        getUser()
    }

    @Disabled
    @Test
    fun testGetUsers() {
        // TODO
    }

    @Test
    fun testRegisterUser() {
        registerUser()
    }

    @Disabled
    @Test
    fun testRefreshToken() {
        // TODO
    }

    @Disabled
    @Test
    fun testGetSimilarUsers() {
        // TODO
    }

    @Disabled
    @Test
    fun testGetDetailedSimilarUser() {
        // TODO
    }

    private fun loginUser(): Map<String, Any> {
        val request = mapOf(
            "username" to "username1",
            "password" to "password1"
        )
        val response = restTemplate.postForEntity(
            "$GATEWAY_URL/users/auth/login",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseAuth = readObjectFromJsonString(response.body!!)
        assertNotNull(responseAuth["user"])
        assertNotNull(responseAuth["accessToken"])
        assertNotNull(responseAuth["refreshToken"])

        val responseUser = responseAuth["user"] as HashMap<*, *>
        assertNotNull(responseUser["id"])
        assertNotNull(responseUser["name"])
        assertNotNull(responseUser["gender"])
        assertEquals(request["username"], responseUser["username"])
        assertNotNull(responseUser["age"])
        assertNotNull(responseUser["city"])
        assertNotNull(responseUser["purpose"])
        assertNotNull(responseUser["activity"])
        assertNotNull(responseUser["role"])

        return responseAuth
    }

    private fun addCategory(): Int {
        val request = mapOf(
            "name" to "TestRemoteCategory$randomNumber"
        )
        val response = restTemplate.postWithToken(
            accessToken,
            "$GATEWAY_URL/categories",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.CREATED, response.statusCode)

        val responseCategory = readObjectFromJsonString(response.body!!)
        assertNotNull(responseCategory["id"])
        assertEquals(request["name"], responseCategory["name"])

        return responseCategory["id"] as Int
    }

    private fun addDefiningTheme(): Int {
        val request = mapOf(
            "name" to "TestRemoteDefiningTheme$randomNumber",
            "fromOpinion" to "No",
            "toOpinion" to "Yes",
            "categoryId" to categoryId
        )
        val response = restTemplate.postWithToken(
            accessToken,
            "$GATEWAY_URL/defining-themes",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.CREATED, response.statusCode)

        val responseDefiningTheme = readObjectFromJsonString(response.body!!)
        assertNotNull(responseDefiningTheme["id"])
        assertEquals(request["name"], responseDefiningTheme["name"])
        assertEquals(request["fromOpinion"], responseDefiningTheme["fromOpinion"])
        assertEquals(request["toOpinion"], responseDefiningTheme["toOpinion"])
        assertEquals(request["categoryId"], responseDefiningTheme["categoryId"])
        assertNotNull(responseDefiningTheme["numberInCategory"])

        return responseDefiningTheme["id"] as Int
    }

    private fun addStatement(): Int {
        val request = mapOf(
            "text" to "TestRemoteStatement$randomNumber",
            "isSupportDefiningTheme" to true,
            "definingThemeId" to definingThemeId,
            "creatorUserId" to userId
        )
        val response = restTemplate.postWithToken(
            accessToken,
            "$GATEWAY_URL/statements",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.CREATED, response.statusCode)

        val responseStatement = readObjectFromJsonString(response.body!!)
        assertNotNull(responseStatement["id"])
        assertEquals(request["text"], responseStatement["text"])
        assertEquals(request["isSupportDefiningTheme"], responseStatement["isSupportDefiningTheme"])
        assertEquals(request["definingThemeId"], responseStatement["definingThemeId"])
        assertEquals(request["creatorUserId"], responseStatement["creatorUserId"])

        return responseStatement["id"] as Int
    }

    private fun registerUser() {
        val request = mapOf(
            "name" to "Alexander",
            "gender" to 0, // MALE
            "username" to "username$randomNumber",
            "password" to "password$randomNumber",
            "repeatedPassword" to "password$randomNumber",
            "email" to "email$randomNumber@gmail.com",
            "age" to 26,
            "city" to "Moscow",
            "purpose" to 2 // ALL_AT_ONCE
        )
        val response = restTemplate.postForEntity(
            "$GATEWAY_URL/users/auth/register",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseAuth = readObjectFromJsonString(response.body!!)
        assertNotNull(responseAuth["user"])
        assertNotNull(responseAuth["accessToken"])
        assertNotNull(responseAuth["refreshToken"])

        val responseUser = responseAuth["user"] as HashMap<*, *>
        assertNotNull(responseUser["id"])
        assertEquals(request["name"], responseUser["name"])
        assertNotNull(responseUser["gender"])
        assertEquals(request["username"], responseUser["username"])
        assertEquals(request["age"], responseUser["age"])
        assertEquals(request["city"], responseUser["city"])
        assertNotNull(responseUser["purpose"])
        assertNotNull(responseUser["activity"])
        assertNotNull(responseUser["role"])
    }

    private fun getUser() {
        val response = restTemplate.getWithToken(
            accessToken,
            "$GATEWAY_URL/users/$userId",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseUser = readObjectFromJsonString(response.body!!)
        assertEquals(userId, responseUser["id"])
        assertNotNull(responseUser["name"])
        assertNotNull(responseUser["gender"])
        assertNotNull(responseUser["username"])
        assertNotNull(responseUser["age"])
        assertNotNull(responseUser["city"])
        assertNotNull(responseUser["purpose"])
        assertNotNull(responseUser["activity"])
        assertNotNull(responseUser["role"])
    }

    private fun processStatementReaction() {
        val request = mapOf(
            "userId" to userId,
            "statementId" to statementId,
            "categoryId" to categoryId,
            "definingThemeId" to definingThemeId,
            "reactionType" to 4, // FULL_MAINTAIN
            "isSupportDefiningTheme" to true
        )
        val response = restTemplate.postWithToken(
            accessToken,
            "$GATEWAY_URL/statements/users/reaction",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    private fun getUserCategories() {
        val response = restTemplate.getWithToken(
            accessToken,
            "$GATEWAY_URL/categories/users?userId=$userId",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseUserCategories = readArrayFromJsonString(response.body!!)
        val newUserCategory = responseUserCategories
            .firstOrNull { it["userId"] == userId && it["categoryId"] == categoryId }
        assertNotNull(newUserCategory)
        assertNotNull(newUserCategory["interest"])
    }

    private fun getUserDefiningThemes() {
        val response = restTemplate.getWithToken(
            accessToken,
            "$GATEWAY_URL/defining-themes/users?userId=$userId",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseUserDefiningThemes = readArrayFromJsonString(response.body!!)
        val newUserDefiningTheme = responseUserDefiningThemes
            .firstOrNull { it["userId"] == userId && it["definingThemeId"] == definingThemeId }
        assertNotNull(newUserDefiningTheme)
        assertNotNull(newUserDefiningTheme["value"])
        assertNotNull(newUserDefiningTheme["interest"])
    }

    fun <T> TestRestTemplate.getWithToken(
        accessToken: String,
        url: String,
        responseType: Class<T>
    ): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.setBearerAuth(accessToken)
        val entity = HttpEntity<Void>(headers)
        return exchange(url, HttpMethod.GET, entity, responseType)
    }

    fun <T> TestRestTemplate.postWithToken(
        accessToken: String,
        url: String,
        body: Any,
        responseType: Class<T>
    ): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(accessToken)
        val entity = HttpEntity(body, headers)
        return exchange(url, HttpMethod.POST, entity, responseType)
    }
}