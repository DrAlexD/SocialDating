package xelagurd.socialdating.client.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.details.StatementDetails
import xelagurd.socialdating.client.data.model.details.StatementReactionDetails
import xelagurd.socialdating.client.data.model.dto.DetailedSimilarUserDto
import xelagurd.socialdating.client.data.model.dto.SimilarUserDto
import xelagurd.socialdating.client.data.model.dto.StatementDto

interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): Response<User>

    @GET("categories")
    suspend fun getCategories(@Query("categoryIds") categoryIds: List<Int>?): Response<List<Category>>

    @GET("categories/users")
    suspend fun getUserCategories(@Query("userId") userId: Int): Response<List<UserCategory>>

    @GET("defining-themes")
    suspend fun getDefiningThemes(
        @Query("definingThemeIds") definingThemeIds: List<Int>?,
        @Query("categoryId") categoryId: Int?
    ): Response<List<DefiningTheme>>

    @GET("defining-themes/users")
    suspend fun getUserDefiningThemes(@Query("userId") userId: Int): Response<List<UserDefiningTheme>>

    @GET("statements")
    suspend fun getStatements(
        @Query("currentUserId") currentUserId: Int,
        @Query("definingThemeIds") definingThemeIds: List<Int>
    ): Response<List<StatementDto>>

    @POST("statements")
    suspend fun addStatement(@Body statementDetails: StatementDetails): Response<StatementDto>

    @POST("statements/users/reaction")
    suspend fun processStatementReaction(
        @Body statementReactionDetails: StatementReactionDetails
    ): Response<Unit>

    @GET("categories/users/similar-users")
    suspend fun getSimilarUsers(
        @Query("currentUserId") currentUserId: Int,
        @Query("categoryIds") categoryIds: List<Int>?
    ): Response<List<SimilarUserDto>>

    @GET("categories/users/detailed-similar-user")
    suspend fun getDetailedSimilarUser(
        @Query("currentUserId") currentUserId: Int,
        @Query("anotherUserId") anotherUserId: Int
    ): Response<DetailedSimilarUserDto>
}