package xelagurd.socialdating.data.network

import javax.inject.Inject
import xelagurd.socialdating.data.model.Category

class RemoteCategoriesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCategories(): List<Category> = apiService.getCategories()
}