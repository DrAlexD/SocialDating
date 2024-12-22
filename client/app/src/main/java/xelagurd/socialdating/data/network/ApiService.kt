package xelagurd.socialdating.data.network

import retrofit2.http.GET
import retrofit2.http.Query
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.Statement

interface ApiService {
    @GET("categories")
    suspend fun getCategories(): List<Category>

    @GET("statements")
    suspend fun getStatements(@Query("categoryId") categoryId: Int): List<Statement>
}