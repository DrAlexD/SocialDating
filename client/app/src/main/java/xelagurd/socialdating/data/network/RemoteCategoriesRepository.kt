package xelagurd.socialdating.data.network

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.Category

@Singleton
class RemoteCategoriesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCategories(): List<Category> = apiService.getCategories()
}