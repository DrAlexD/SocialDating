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
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarUser
import xelagurd.socialdating.client.data.model.additional.SimilarUser
import xelagurd.socialdating.client.data.model.additional.StatementReactionDetails
import xelagurd.socialdating.client.data.model.details.StatementDetails

interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): Response<User>

    @GET("users")
    suspend fun getUsers(@Query("userIds") userIds: List<Int>): Response<List<User>>

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("categories/users")
    suspend fun getUserCategories(@Query("userId") userId: Int): Response<List<UserCategory>>

    @GET("defining-themes")
    suspend fun getDefiningThemes(@Query("categoryId") categoryId: Int?): Response<List<DefiningTheme>>

    @GET("defining-themes/users")
    suspend fun getUserDefiningThemes(@Query("userId") userId: Int): Response<List<UserDefiningTheme>>

    @GET("statements")
    suspend fun getStatements(
        @Query("userId") userId: Int,
        @Query("definingThemeIds") definingThemeIds: List<Int>
    ): Response<List<Statement>>

    @POST("statements")
    suspend fun addStatement(@Body statementDetails: StatementDetails): Response<Statement>

    @POST("statements/users/reaction")
    suspend fun processStatementReaction(
        @Body statementReactionDetails: StatementReactionDetails
    ): Response<Unit>

    @GET("categories/users/similar-users")
    suspend fun getSimilarUsers(
        @Query("userId") userId: Int,
        @Query("categoryIds") categoryIds: List<Int>?
    ): Response<List<SimilarUser>>

    @GET("categories/users/detailed-similar-user")
    suspend fun getDetailedSimilarUser(
        @Query("currentUserId") currentUserId: Int,
        @Query("anotherUserId") anotherUserId: Int
    ): Response<DetailedSimilarUser>
}