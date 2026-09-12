package xelagurd.socialdating.client

import retrofit2.Response
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.details.StatementDetails
import xelagurd.socialdating.client.data.model.details.StatementReactionDetails
import xelagurd.socialdating.client.data.model.dto.DetailedSimilarUserDto
import xelagurd.socialdating.client.data.model.dto.PageDto
import xelagurd.socialdating.client.data.model.dto.SimilarUserDto
import xelagurd.socialdating.client.data.model.dto.StatementDto
import xelagurd.socialdating.client.data.remote.ApiService

class FakeApiService : ApiService {
    override suspend fun getUser(userId: Int): Response<User> =
        Response.success(FakeData.mainUser)

    override suspend fun getCategories(categoryIds: List<Int>?): Response<List<Category>> =
        Response.success(FakeData.categories)

    override suspend fun getUserCategories(userId: Int): Response<List<UserCategory>> =
        Response.success(FakeData.userCategories)

    override suspend fun getDefiningThemes(
        definingThemeIds: List<Int>?,
        categoryId: Int?
    ): Response<List<DefiningTheme>> =
        Response.success(FakeData.definingThemes)

    override suspend fun getUserDefiningThemes(userId: Int): Response<List<UserDefiningTheme>> =
        Response.success(FakeData.userDefiningThemes)

    override suspend fun getStatements(
        currentUserId: Int,
        definingThemeIds: List<Int>,
        cursor: String?,
        size: Int
    ): Response<PageDto<StatementDto>> =
        pageFrom(FakeData.statementDtos, cursor, size)

    override suspend fun addStatement(
        statementDetails: StatementDetails
    ): Response<StatementDto> =
        Response.success(FakeData.newStatement)

    override suspend fun processStatementReaction(
        statementReactionDetails: StatementReactionDetails
    ): Response<Unit> =
        Response.success(null)

    override suspend fun getSimilarUsers(
        currentUserId: Int,
        categoryIds: List<Int>?,
        cursor: String?,
        size: Int
    ): Response<PageDto<SimilarUserDto>> =
        pageFrom(FakeData.similarUsers, cursor, size)

    override suspend fun getDetailedSimilarUser(
        currentUserId: Int,
        anotherUserId: Int
    ): Response<DetailedSimilarUserDto> =
        Response.success(FakeData.detailedSimilarUser)

    private fun <T> pageFrom(entities: List<T>, cursor: String?, size: Int): Response<PageDto<T>> {
        val firstIndex = cursor?.toInt() ?: 0
        val pageEntities = entities.drop(firstIndex).take(size)
        val nextIndex = firstIndex + pageEntities.size

        return when {
            pageEntities.isEmpty() -> Response.success(null)
            else -> Response.success(
                PageDto(
                    content = pageEntities,
                    nextCursor = nextIndex.toString().takeIf { nextIndex < entities.size }
                )
            )
        }
    }
}