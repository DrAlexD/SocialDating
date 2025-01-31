package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.remote.ApiService

@Singleton
class RemoteCategoriesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getCategories() =
        apiService.getCategories()
}