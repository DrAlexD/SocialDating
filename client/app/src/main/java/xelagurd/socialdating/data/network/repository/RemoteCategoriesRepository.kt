package xelagurd.socialdating.data.network.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.network.ApiService

@Singleton
class RemoteCategoriesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCategories(): List<Category> = apiService.getCategories()
}