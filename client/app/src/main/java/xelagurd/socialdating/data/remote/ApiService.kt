package xelagurd.socialdating.data.remote

import retrofit2.http.GET
import retrofit2.http.Query
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.User

interface ApiService {
    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("definingThemes")
    suspend fun getDefiningThemes(@Query("categoryId") categoryId: Int): List<DefiningTheme>

    @GET("statements")
    suspend fun getStatements(@Query("definingThemeIds") definingThemeIds: List<Int>): List<Statement>

    @GET("users")
    suspend fun getUser(@Query("userId") userId: Int): User
}