package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.model.DataUtils.SIMILAR_USERS_PAGE_SIZE
import xelagurd.socialdating.client.data.remote.ApiService

@Singleton
class RemoteUserCategoriesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserCategories(userId: Int) =
        apiService.getUserCategories(userId)

    suspend fun getSimilarUsers(
        currentUserId: Int,
        categoryIds: List<Int>? = null,
        cursor: String? = null,
        size: Int = SIMILAR_USERS_PAGE_SIZE
    ) =
        apiService.getSimilarUsers(currentUserId, categoryIds, cursor, size)

    suspend fun getDetailedSimilarUser(currentUserId: Int, anotherUserId: Int) =
        apiService.getDetailedSimilarUser(currentUserId, anotherUserId)
}
