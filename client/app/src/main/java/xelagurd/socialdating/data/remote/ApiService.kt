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
    @POST("users/auth/login")
    suspend fun loginUser(@Body loginDetails: LoginDetails): User?

    @POST("users/auth/register")
    suspend fun registerUser(@Body registrationDetails: RegistrationDetails): User?

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User?

    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("categories/users/{id}")
    suspend fun getUserCategories(@Path("id") userId: Int): List<UserCategory>

    @GET("defining-themes")
    suspend fun getDefiningThemes(@Query("categoryIds") categoryIds: List<Int>): List<DefiningTheme>

    @GET("defining-themes/users")
    suspend fun getUserDefiningThemes(@Query("userCategoryIds") userCategoryIds: List<Int>): List<UserDefiningTheme>

    @GET("statements")
    suspend fun getStatements(@Query("definingThemeIds") definingThemeIds: List<Int>): List<Statement>

    @POST("statements")
    suspend fun addStatement(@Body statementDetails: StatementDetails): Statement?

    @POST("statements/reaction")
    suspend fun postStatementReaction(@Body statementReaction: StatementReaction)
}