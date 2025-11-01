package xelagurd.socialdating.client.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.UserStatement
import xelagurd.socialdating.client.data.model.additional.StatementReactionDetails
import xelagurd.socialdating.client.data.model.details.StatementDetails

interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): Response<User>

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("categories/users")
    suspend fun getUserCategories(@Query("userId") userId: Int): Response<List<UserCategory>>

    @GET("defining-themes")
    suspend fun getDefiningThemes(@Query("categoryIds") categoryIds: List<Int>): Response<List<DefiningTheme>>

    @GET("defining-themes/users")
    suspend fun getUserDefiningThemes(@Query("userId") userId: Int): Response<List<UserDefiningTheme>>

    @GET("statements")
    suspend fun getStatements(
        @Query("userId") userId: Int,
        @Query("definingThemeIds") definingThemeIds: List<Int>
    ): Response<List<Statement>>

    @GET("statements/users")
    suspend fun getUserStatements(
        @Query("userId") userId: Int,
        @Query("definingThemeIds") definingThemeIds: List<Int>
    ): Response<List<UserStatement>>

    @POST("statements")
    suspend fun addStatement(@Body statementDetails: StatementDetails): Response<Statement>

    @POST("statements/users/reaction")
    suspend fun addStatementReaction(@Body statementReactionDetails: StatementReactionDetails): Response<UserStatement>
}