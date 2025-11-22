package xelagurd.socialdating.client

import retrofit2.Response
import xelagurd.socialdating.client.data.fake.FakeData
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.additional.DetailedSimilarUser
import xelagurd.socialdating.client.data.model.additional.SimilarUser
import xelagurd.socialdating.client.data.model.additional.StatementReactionDetails
import xelagurd.socialdating.client.data.model.details.StatementDetails
import xelagurd.socialdating.client.data.remote.ApiService

class FakeApiService : ApiService {
    override suspend fun getUser(userId: Int): Response<User> =
        Response.success(FakeData.mainUser)

    override suspend fun getUsers(userIds: List<Int>):  Response<List<User>> =
        Response.success(FakeData.users)

    override suspend fun getCategories(): Response<List<Category>> =
        Response.success(FakeData.categories)

    override suspend fun getUserCategories(userId: Int): Response<List<UserCategory>> =
        Response.success(FakeData.userCategories)

    override suspend fun getDefiningThemes(categoryId: Int?): Response<List<DefiningTheme>> =
        Response.success(FakeData.definingThemes)

    override suspend fun getUserDefiningThemes(userId: Int): Response<List<UserDefiningTheme>> =
        Response.success(FakeData.userDefiningThemes)

    override suspend fun getStatements(
        userId: Int,
        definingThemeIds: List<Int>
    ): Response<List<Statement>> =
        Response.success(FakeData.statements)

    override suspend fun addStatement(statementDetails: StatementDetails): Response<Statement> =
        Response.success(FakeData.newStatement)

    override suspend fun processStatementReaction(
        statementReactionDetails: StatementReactionDetails
    ): Response<Unit> =
        Response.success(null)

    override suspend fun getSimilarUsers(
        userId: Int,
        categoryIds: List<Int>?
    ): Response<List<SimilarUser>> =
        Response.success(FakeData.similarUsers)

    override suspend fun getDetailedSimilarUser(
        currentUserId: Int,
        anotherUserId: Int
    ): Response<DetailedSimilarUser> =
        Response.success(FakeData.detailedSimilarUser)
}