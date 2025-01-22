package xelagurd.socialdating.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.UserCategory
import xelagurd.socialdating.data.model.UserDefiningTheme
import xelagurd.socialdating.data.model.additional.StatementReaction
import xelagurd.socialdating.data.model.details.LoginDetails
import xelagurd.socialdating.data.model.details.RegistrationDetails
import xelagurd.socialdating.data.model.details.StatementDetails

interface ApiService {
    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("defining-themes")
    suspend fun getDefiningThemes(@Query("categoryIds") categoryIds: List<Int>): List<DefiningTheme>

    @GET("statements")
    suspend fun getStatements(@Query("definingThemeIds") definingThemeIds: List<Int>): List<Statement>

    @POST("statements/reaction")
    suspend fun postStatementReaction(@Body statementReaction: StatementReaction)

    @POST("statements")
    suspend fun statementAdding(@Body statementDetails: StatementDetails): Statement?

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User?

    @GET("user-categories")
    suspend fun getUserCategories(@Query("userId") userId: Int): List<UserCategory>

    @GET("user-defining-themes")
    suspend fun getUserDefiningThemes(@Query("userCategoryIds") userCategoryIds: List<Int>): List<UserDefiningTheme>

    @POST("users/login")
    suspend fun loginUser(@Body loginDetails: LoginDetails): User?

    @POST("users/register")
    suspend fun registerUser(@Body registrationDetails: RegistrationDetails): User?
}