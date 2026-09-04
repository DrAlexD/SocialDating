package xelagurd.socialdating.server.integrationTest

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import xelagurd.socialdating.server.model.DefaultDataProperties.CATEGORY_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_INTEREST_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_COEFFICIENT
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_HIGH_BORDER
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_INITIAL
import xelagurd.socialdating.server.model.DefaultDataProperties.DEFINING_THEME_VALUE_STEP
import xelagurd.socialdating.server.model.DefaultDataProperties.GATEWAY_URL
import xelagurd.socialdating.server.model.enums.StatementReactionType
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_MAINTAIN
import xelagurd.socialdating.server.model.enums.StatementReactionType.FULL_NO_MAINTAIN
import xelagurd.socialdating.server.utils.TestUtils.readArrayFromJsonString
import xelagurd.socialdating.server.utils.TestUtils.readObject
import xelagurd.socialdating.server.utils.TestUtils.readObjectFromJsonString
import xelagurd.socialdating.server.utils.TestUtils.toRequestParams

/**
 * Covers the only flow that spans several microservices: a statement reaction is saved by statements-service
 * and then, through the chain of Kafka events, updates the user defining themes in defining-themes-service
 * and finally the interest and the maintained lists of every affected user category in categories-service.
 *
 * Everything that is handled by a single microservice is covered by the corresponding *MicroserviceTest.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class IntegrationTest {
    private val restTemplate = TestRestTemplate()

    // the test is run against a long living server, so all the created data must be named uniquely
    private val uniqueNumber = System.currentTimeMillis()

    // the same reaction types make the users similar, the different ones make them opposite
    private val adminReactionTypes = listOf(FULL_MAINTAIN, FULL_MAINTAIN, FULL_NO_MAINTAIN)
    private val userReactionTypes = listOf(FULL_MAINTAIN, FULL_MAINTAIN, FULL_MAINTAIN)

    private val definingThemesNumber = adminReactionTypes.size
    private val definingThemeValueDiff = DEFINING_THEME_VALUE_STEP * DEFINING_THEME_VALUE_COEFFICIENT

    // the number of reactions which is needed to move a defining theme value to the maintained border
    private val statementsNumber =
        (DEFINING_THEME_VALUE_HIGH_BORDER - DEFINING_THEME_VALUE_INITIAL) / definingThemeValueDiff

    private val similarDefiningThemesNumber = adminReactionTypes
        .zip(userReactionTypes)
        .count { (adminReactionType, userReactionType) -> adminReactionType == userReactionType }
    private val oppositeDefiningThemesNumber = definingThemesNumber - similarDefiningThemesNumber

    private lateinit var admin: AuthorizedUser
    private lateinit var user: AuthorizedUser

    private var categoryId = -1
    private val definingThemeIds = mutableListOf<Int>()
    private val allDefiningThemeIds
        get() = definingThemeIds + extraDefiningThemeId + anotherCategoryDefiningThemeId
    private val statementIds = mutableMapOf<Int, List<Int>>()

    private var extraDefiningThemeId = -1
    private var anotherCategoryId = -1
    private var anotherCategoryDefiningThemeId = -1
    private var multiThemeStatementId = -1

    @BeforeAll
    fun initializeData() {
        admin = loginUser()
        user = registerUser()

        categoryId = addCategory("TestRemoteCategory$uniqueNumber")
        repeat(definingThemesNumber) { definingThemeIds += addDefiningTheme(categoryId, it + 1) }
        definingThemeIds.forEach { definingThemeId ->
            statementIds[definingThemeId] = List(statementsNumber) {
                addStatement("TestRemoteStatement${uniqueNumber}_${definingThemeId}_${it + 1}", definingThemeId to true)
            }
        }

        extraDefiningThemeId = addDefiningTheme(categoryId, definingThemesNumber + 1)
        anotherCategoryId = addCategory("TestRemoteAnotherCategory$uniqueNumber")
        anotherCategoryDefiningThemeId = addDefiningTheme(anotherCategoryId, 1)
        multiThemeStatementId = addStatement(
            "TestRemoteMultiThemeStatement$uniqueNumber",
            extraDefiningThemeId to true,
            anotherCategoryDefiningThemeId to false
        )
    }

    @Order(1)
    @Test
    fun processStatementReaction_validData_updatesUserDefiningThemes() {
        processStatementReactions(user, userReactionTypes)
        processStatementReactions(admin, adminReactionTypes)
    }

    @Order(2)
    @Test
    fun processStatementReaction_statementWithSeveralDefiningThemes_updatesEveryOfThem() {
        listOf(admin, user).forEach { authorizedUser ->
            processStatementReaction(authorizedUser, multiThemeStatementId, FULL_MAINTAIN)

            awaitAssertion {
                assertEquals(
                    DEFINING_THEME_VALUE_INITIAL + definingThemeValueDiff,
                    getUserDefiningTheme(authorizedUser, extraDefiningThemeId)["value"]
                )
                assertEquals(
                    DEFINING_THEME_VALUE_INITIAL - definingThemeValueDiff,
                    getUserDefiningTheme(authorizedUser, anotherCategoryDefiningThemeId)["value"]
                )
            }
        }
    }

    @Order(3)
    @Test
    fun getUserCategories_afterReactions_increasedInterest() {
        // every reaction increases the interest of each affected category by a single step,
        // no matter how many defining themes of that category are affected
        val expectedInterest = CATEGORY_INTEREST_STEP * (definingThemesNumber * statementsNumber + 1)

        listOf(admin, user).forEach { authorizedUser ->
            awaitAssertion {
                assertEquals(expectedInterest, getUserCategory(authorizedUser, categoryId)["interest"])
                assertEquals(
                    CATEGORY_INTEREST_STEP,
                    getUserCategory(authorizedUser, anotherCategoryId)["interest"]
                )
            }
        }
    }

    @Order(4)
    @Test
    fun getStatements_afterReactions_noContent() {
        listOf(admin, user).forEach { authorizedUser ->
            assertEquals(HttpStatus.NO_CONTENT, getStatements(authorizedUser).statusCode)
        }
    }

    @Order(5)
    @Test
    fun getDetailedSimilarUser_afterReactions_returnsSimilarityFromMaintainedLists() {
        val expectedDefiningThemesSimilarity = adminReactionTypes
            .zip(userReactionTypes)
            .mapIndexed { index, (adminReactionType, userReactionType) ->
                // defining themes are identified by their number in category, not by their id
                "${index + 1}" to if (adminReactionType == userReactionType) "SIMILAR" else "OPPOSITE"
            }
            .toMap()

        awaitAssertion {
            val response = restTemplate.getWithToken(
                user,
                "$GATEWAY_URL/categories/users/detailed-similar-user" +
                        "?currentUserId=${user.id}&anotherUserId=${admin.id}",
                String::class.java
            )
            assertEquals(HttpStatus.OK, response.statusCode)

            val responseDetailedSimilarUser = readObjectFromJsonString(response.body!!)
            assertEquals(similarDefiningThemesNumber, responseDetailedSimilarUser["similarNumber"])
            assertEquals(oppositeDefiningThemesNumber, responseDetailedSimilarUser["oppositeNumber"])

            val responseCategories = responseDetailedSimilarUser.readObject("categories")
            assertEquals(setOf(categoryId.toString()), responseCategories.keys)

            val responseCategory = responseCategories.readObject(categoryId.toString())
            assertEquals("SIMILAR", responseCategory["similarityType"])
            assertEquals(similarDefiningThemesNumber, responseCategory["similarNumber"])
            assertEquals(oppositeDefiningThemesNumber, responseCategory["oppositeNumber"])
            assertEquals(expectedDefiningThemesSimilarity, responseCategory.readDefiningThemesSimilarity())
        }
    }

    @Order(6)
    @Test
    fun getSimilarUsers_afterReactions_returnsSimilarUserDtoFromUsersService() {
        val response = restTemplate.getWithToken(
            user,
            "$GATEWAY_URL/categories/users/similar-users?currentUserId=${user.id}",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseSimilarUser = readArrayFromJsonString(response.body!!)
            .firstOrNull { it["id"] == admin.id }
        assertNotNull(responseSimilarUser)

        assertEquals(similarDefiningThemesNumber, responseSimilarUser["similarNumber"])
        assertEquals(oppositeDefiningThemesNumber, responseSimilarUser["oppositeNumber"])

        assertEquals(admin.data["name"], responseSimilarUser["name"])
        assertEquals(admin.data["gender"], responseSimilarUser["gender"])
        assertEquals(admin.data["age"], responseSimilarUser["age"])
        assertEquals(admin.data["city"], responseSimilarUser["city"])
        assertEquals(admin.data["purpose"], responseSimilarUser["purpose"])
    }

    @Order(7)
    @Test
    fun getCategories_russianLanguage_returnsRussianName() {
        val englishCategory = getCategory(ENGLISH_LANGUAGE)
        val russianCategory = getCategory(RUSSIAN_LANGUAGE)

        assertEquals("$RUSSIAN_NAME_PREFIX${englishCategory["name"]}", russianCategory["name"])
    }

    @Order(8)
    @Test
    fun getDefiningThemes_russianLanguage_returnsRussianNameAndOpinions() {
        val englishDefiningTheme = getDefiningTheme(ENGLISH_LANGUAGE)
        val russianDefiningTheme = getDefiningTheme(RUSSIAN_LANGUAGE)

        assertEquals("$RUSSIAN_NAME_PREFIX${englishDefiningTheme["name"]}", russianDefiningTheme["name"])
        assertEquals("Нет", russianDefiningTheme["fromOpinion"])
        assertEquals("Да", russianDefiningTheme["toOpinion"])
    }

    private fun getCategory(language: String): Map<String, Any> {
        val response = restTemplate.getWithToken(
            admin,
            "$GATEWAY_URL/categories?categoryIds=$categoryId",
            String::class.java,
            language
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseCategory = readArrayFromJsonString(response.body!!)
            .firstOrNull { it["id"] == categoryId }
        assertNotNull(responseCategory)

        return responseCategory
    }

    private fun getDefiningTheme(language: String): Map<String, Any> {
        val definingThemeId = definingThemeIds.first()
        val response = restTemplate.getWithToken(
            admin,
            "$GATEWAY_URL/defining-themes?definingThemeIds=$definingThemeId",
            String::class.java,
            language
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseDefiningTheme = readArrayFromJsonString(response.body!!)
            .firstOrNull { it["id"] == definingThemeId }
        assertNotNull(responseDefiningTheme)

        return responseDefiningTheme
    }

    private fun loginUser(): AuthorizedUser {
        val request = mapOf(
            "username" to ADMIN_USERNAME,
            "password" to ADMIN_PASSWORD
        )
        val response = restTemplate.postForEntity(
            "$GATEWAY_URL/users/auth/login",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseAuth = readObjectFromJsonString(response.body!!)
        assertEquals(request["username"], responseAuth.readObject("user")["username"])

        return responseAuth.toAuthorizedUser()
    }

    private fun registerUser(): AuthorizedUser {
        val request = mapOf(
            "name" to "Alexander",
            "gender" to "MALE",
            "username" to "user$uniqueNumber",
            "password" to "pass$uniqueNumber",
            "email" to "email$uniqueNumber@gmail.com",
            "age" to 26,
            "city" to "Moscow",
            "purpose" to "ALL_AT_ONCE"
        )
        val response = restTemplate.postForEntity(
            "$GATEWAY_URL/users/auth/register",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseAuth = readObjectFromJsonString(response.body!!)
        val responseUser = responseAuth.readObject("user")
        assertEquals(request["name"], responseUser["name"])
        assertEquals(request["username"], responseUser["username"])
        assertEquals(request["age"], responseUser["age"])
        assertEquals(request["city"], responseUser["city"])

        return responseAuth.toAuthorizedUser()
    }

    private fun addCategory(name: String): Int {
        val request = mapOf(
            "nameEn" to name,
            "nameRu" to "$RUSSIAN_NAME_PREFIX$name"
        )
        val response = restTemplate.postWithToken(
            admin,
            "$GATEWAY_URL/categories",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.CREATED, response.statusCode)

        val responseCategory = readObjectFromJsonString(response.body!!)
        assertNotNull(responseCategory["id"])
        assertEquals(request["nameEn"], responseCategory["name"])

        return responseCategory["id"] as Int
    }

    private fun addDefiningTheme(categoryId: Int, numberInCategory: Int): Int {
        val name = "TestRemoteDefiningTheme${uniqueNumber}_${categoryId}_$numberInCategory"
        val request = mapOf(
            "nameEn" to name,
            "nameRu" to "$RUSSIAN_NAME_PREFIX$name",
            "fromOpinionEn" to "No",
            "fromOpinionRu" to "Нет",
            "toOpinionEn" to "Yes",
            "toOpinionRu" to "Да",
            "categoryId" to categoryId
        )
        val response = restTemplate.postWithToken(
            admin,
            "$GATEWAY_URL/defining-themes",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.CREATED, response.statusCode)

        val responseDefiningTheme = readObjectFromJsonString(response.body!!)
        assertNotNull(responseDefiningTheme["id"])
        assertEquals(request["nameEn"], responseDefiningTheme["name"])
        assertEquals(request["fromOpinionEn"], responseDefiningTheme["fromOpinion"])
        assertEquals(request["toOpinionEn"], responseDefiningTheme["toOpinion"])
        assertEquals(categoryId, responseDefiningTheme["categoryId"])
        assertEquals(numberInCategory, responseDefiningTheme["numberInCategory"])

        return responseDefiningTheme["id"] as Int
    }

    private fun addStatement(text: String, vararg definingThemes: Pair<Int, Boolean>): Int {
        val requestDefiningThemes = definingThemes.map { (definingThemeId, isSupportDefiningTheme) ->
            mapOf(
                "definingThemeId" to definingThemeId,
                "isSupportDefiningTheme" to isSupportDefiningTheme
            )
        }
        val request = mapOf(
            "text" to text,
            "definingThemes" to requestDefiningThemes,
            "creatorUserId" to admin.id
        )
        val response = restTemplate.postWithToken(
            admin,
            "$GATEWAY_URL/statements",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.CREATED, response.statusCode)

        val responseStatement = readObjectFromJsonString(response.body!!)
        assertNotNull(responseStatement["id"])
        assertEquals(request["text"], responseStatement["text"])
        assertEquals(requestDefiningThemes, responseStatement["definingThemes"])
        assertEquals(admin.id, responseStatement["creatorUserId"])

        return responseStatement["id"] as Int
    }

    private fun processStatementReactions(
        authorizedUser: AuthorizedUser,
        reactionTypes: List<StatementReactionType>
    ) {
        definingThemeIds.forEachIndexed { definingThemeIndex, definingThemeId ->
            val reactionType = reactionTypes[definingThemeIndex]

            statementIds.getValue(definingThemeId).forEachIndexed { statementIndex, statementId ->
                processStatementReaction(authorizedUser, statementId, reactionType)

                // the next reaction is sent only after the current one is processed by all the microservices
                awaitAssertion {
                    val responseUserDefiningTheme = getUserDefiningTheme(authorizedUser, definingThemeId)
                    assertEquals(
                        definingThemeValue(reactionType, statementIndex + 1),
                        responseUserDefiningTheme["value"]
                    )
                    assertEquals(
                        DEFINING_THEME_INTEREST_STEP * (statementIndex + 1),
                        responseUserDefiningTheme["interest"]
                    )
                }
            }
        }
    }

    private fun processStatementReaction(
        authorizedUser: AuthorizedUser,
        statementId: Int,
        reactionType: StatementReactionType
    ) {
        val request = mapOf(
            "userId" to authorizedUser.id,
            "statementId" to statementId,
            "reactionType" to reactionType.name
        )
        val response = restTemplate.postWithToken(
            authorizedUser,
            "$GATEWAY_URL/statements/users/reaction",
            request,
            String::class.java
        )
        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    private fun getStatements(authorizedUser: AuthorizedUser) =
        restTemplate.getWithToken(
            authorizedUser,
            "$GATEWAY_URL/statements" +
                    "?currentUserId=${authorizedUser.id}" +
                    "&definingThemeIds=${allDefiningThemeIds.toRequestParams()}",
            String::class.java
        )

    private fun getUserCategory(authorizedUser: AuthorizedUser, categoryId: Int): Map<String, Any> {
        val response = restTemplate.getWithToken(
            authorizedUser,
            "$GATEWAY_URL/categories/users?userId=${authorizedUser.id}",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseUserCategory = readArrayFromJsonString(response.body!!)
            .firstOrNull { it["categoryId"] == categoryId }
        assertNotNull(responseUserCategory)

        return responseUserCategory
    }

    private fun getUserDefiningTheme(authorizedUser: AuthorizedUser, definingThemeId: Int): Map<String, Any> {
        val response = restTemplate.getWithToken(
            authorizedUser,
            "$GATEWAY_URL/defining-themes/users?userId=${authorizedUser.id}",
            String::class.java
        )
        assertEquals(HttpStatus.OK, response.statusCode)

        val responseUserDefiningTheme = readArrayFromJsonString(response.body!!)
            .firstOrNull { it["definingThemeId"] == definingThemeId }
        assertNotNull(responseUserDefiningTheme)

        return responseUserDefiningTheme
    }

    private fun definingThemeValue(reactionType: StatementReactionType, reactionsNumber: Int) =
        when (reactionType) {
            FULL_MAINTAIN -> DEFINING_THEME_VALUE_INITIAL + reactionsNumber * definingThemeValueDiff
            else -> DEFINING_THEME_VALUE_INITIAL - reactionsNumber * definingThemeValueDiff
        }

    private fun Map<String, Any>.toAuthorizedUser(): AuthorizedUser {
        assertNotNull(this["accessToken"])
        assertNotNull(this["refreshToken"])

        val responseUser = readObject("user")
        assertNotNull(responseUser["id"])

        return AuthorizedUser(responseUser["id"] as Int, this["accessToken"] as String, responseUser)
    }

    private fun Map<String, Any>.readDefiningThemesSimilarity() =
        readObject("definingThemes")
            .mapValues { (_, definingTheme) -> (definingTheme as Map<*, *>)["similarityType"] }

    private fun awaitAssertion(block: () -> Unit) {
        val deadline = System.currentTimeMillis() + AWAIT_TIMEOUT_MILLIS

        while (true) {
            try {
                return block()
            } catch (e: AssertionError) {
                if (System.currentTimeMillis() > deadline) throw e
                Thread.sleep(AWAIT_INTERVAL_MILLIS)
            }
        }
    }

    private fun <T> TestRestTemplate.getWithToken(
        authorizedUser: AuthorizedUser,
        url: String,
        responseType: Class<T>,
        language: String = ENGLISH_LANGUAGE
    ): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.setBearerAuth(authorizedUser.accessToken)
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, language)
        return exchange(url, HttpMethod.GET, HttpEntity<Void>(headers), responseType)
    }

    private fun <T> TestRestTemplate.postWithToken(
        authorizedUser: AuthorizedUser,
        url: String,
        body: Any,
        responseType: Class<T>,
        language: String = ENGLISH_LANGUAGE
    ): ResponseEntity<T> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.setBearerAuth(authorizedUser.accessToken)
        headers.set(HttpHeaders.ACCEPT_LANGUAGE, language)
        return exchange(url, HttpMethod.POST, HttpEntity(body, headers), responseType)
    }

    private data class AuthorizedUser(val id: Int, val accessToken: String, val data: Map<String, Any>)

    companion object {
        private const val ADMIN_USERNAME = "username1"
        private const val ADMIN_PASSWORD = "password1"

        private const val RUSSIAN_NAME_PREFIX = "Рус"

        private const val ENGLISH_LANGUAGE = "en"
        private const val RUSSIAN_LANGUAGE = "ru"

        private const val AWAIT_TIMEOUT_MILLIS = 30_000L
        private const val AWAIT_INTERVAL_MILLIS = 200L
    }
}
