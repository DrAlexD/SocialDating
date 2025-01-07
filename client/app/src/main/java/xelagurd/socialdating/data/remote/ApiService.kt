package xelagurd.socialdating.data.remote

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.enums.StatementReactionType
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.UserCategory
import xelagurd.socialdating.data.model.UserDefiningTheme

interface ApiService {
    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("definingThemes")
    suspend fun getDefiningThemes(@Query("categoryId") categoryId: Int): List<DefiningTheme>

    @GET("definingThemes")
    suspend fun getDefiningThemes(@Query("categoryIds") categoryId: List<Int>): List<DefiningTheme>

    @GET("statements")
    suspend fun getStatements(@Query("definingThemeIds") definingThemeIds: List<Int>): List<Statement>

    @POST("statements")
    suspend fun postStatementReaction(
        @Query("userId") userId: Int,
        @Query("statementId") statementId: Int,
        @Query("reactionType") reactionType: StatementReactionType
    )

    @GET("users")
    suspend fun getUser(@Query("userId") userId: Int): User

    @GET("userCategories")
    suspend fun getUserCategories(@Query("userId") userId: Int): List<UserCategory>

    @GET("userDefiningThemes")
    suspend fun getUserDefiningThemes(@Query("userCategoryIds") userCategoryIds: List<Int>): List<UserDefiningTheme>

    @GET("users")
    suspend fun logInUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): User
}