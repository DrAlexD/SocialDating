package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.UserCategory
import xelagurd.socialdating.data.remote.ApiService

@Singleton
class RemoteUserCategoriesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserCategories(userId: Int): List<UserCategory> =
        apiService.getUserCategories(userId)
}