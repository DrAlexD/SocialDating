package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.model.DataUtils.SIMILAR_USERS_PAGE_SIZE
import xelagurd.socialdating.client.data.model.details.SimilarUsersFilterDetails
import xelagurd.socialdating.client.data.remote.ApiService

@Singleton
class RemoteUserCategoriesRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserCategories(userId: Int) =
        apiService.getUserCategories(userId)

    suspend fun getSimilarUsers(
        currentUserId: Int,
        similarUsersFilterDetails: SimilarUsersFilterDetails = SimilarUsersFilterDetails(),
        cursor: String? = null,
        size: Int = SIMILAR_USERS_PAGE_SIZE
    ) =
        apiService.getSimilarUsers(
            currentUserId = currentUserId,
            categoryIds = similarUsersFilterDetails.categoryIds.toList(),
            minDifferenceNumber = similarUsersFilterDetails.minDifferenceNumber,
            maxDifferenceNumber = similarUsersFilterDetails.maxDifferenceNumber,
            genders = similarUsersFilterDetails.genders.toList(),
            ageMin = similarUsersFilterDetails.ageMin,
            ageMax = similarUsersFilterDetails.ageMax,
            purposes = similarUsersFilterDetails.purposes.toList(),
            isSameCity = similarUsersFilterDetails.isSameCity,
            cursor = cursor,
            size = size
        )

    suspend fun getDetailedSimilarUser(currentUserId: Int, anotherUserId: Int) =
        apiService.getDetailedSimilarUser(currentUserId, anotherUserId)
}
