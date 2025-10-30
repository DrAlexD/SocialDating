package xelagurd.socialdating.client

import retrofit2.Response
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.UserStatement
import xelagurd.socialdating.client.data.model.additional.StatementReactionDetails
import xelagurd.socialdating.client.data.model.details.StatementDetails
import xelagurd.socialdating.client.data.remote.ApiService

class FakeApiService : ApiService {
    override suspend fun getUser(userId: Int): Response<User> =
        Response.success(FakeDataSource.users[0])

    override suspend fun getCategories(): Response<List<Category>> =
        Response.success(FakeDataSource.categories)

    override suspend fun getUserCategories(userId: Int): Response<List<UserCategory>> =
        Response.success(FakeDataSource.userCategories)

    override suspend fun getDefiningThemes(categoryIds: List<Int>): Response<List<DefiningTheme>> =
        Response.success(FakeDataSource.definingThemes)

    override suspend fun getUserDefiningThemes(userCategoryIds: List<Int>): Response<List<UserDefiningTheme>> =
        Response.success(FakeDataSource.userDefiningThemes)

    override suspend fun getStatements(
        userId: Int,
        definingThemeIds: List<Int>
    ): Response<List<Statement>> =
        Response.success(FakeDataSource.statements)

    override suspend fun getUserStatements(
        userId: Int,
        definingThemeIds: List<Int>
    ): Response<List<UserStatement>> =
        Response.success(FakeDataSource.userStatements)

    override suspend fun addStatement(statementDetails: StatementDetails): Response<Statement> =
        Response.success(FakeDataSource.newStatement)

    override suspend fun addStatementReaction(statementReactionDetails: StatementReactionDetails): Response<UserStatement> =
        Response.success(FakeDataSource.newUserStatement)
}