package xelagurd.socialdating.data.network

import retrofit2.http.GET
import xelagurd.socialdating.data.model.Category

interface ApiService {
    @GET("categories")
    suspend fun getCategories(): List<Category>
}