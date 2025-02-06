package xelagurd.socialdating.client

import retrofit2.Response
import xelagurd.socialdating.client.data.fake.FakeDataSource
import xelagurd.socialdating.client.data.model.Category
import xelagurd.socialdating.client.data.model.DefiningTheme
import xelagurd.socialdating.client.data.model.Statement
import xelagurd.socialdating.client.data.model.User
import xelagurd.socialdating.client.data.model.UserCategory
import xelagurd.socialdating.client.data.model.UserDefiningTheme
import xelagurd.socialdating.client.data.model.additional.StatementReactionDetails
import xelagurd.socialdating.client.data.model.details.LoginDetails
import xelagurd.socialdating.client.data.model.details.RegistrationDetails
import xelagurd.socialdating.client.data.model.details.StatementDetails
import xelagurd.socialdating.client.data.remote.ApiService

class FakeApiService : ApiService {
    override suspend fun loginUser(loginDetails: LoginDetails): Response<User> =
        Response.success(FakeDataSource.users[0])

    override suspend fun registerUser(registrationDetails: RegistrationDetails): Response<User> =
        Response.success(FakeDataSource.users[0])

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

    override suspend fun getStatements(definingThemeIds: List<Int>): Response<List<Statement>> =
        Response.success(FakeDataSource.statements)

    override suspend fun addStatement(statementDetails: StatementDetails): Response<Statement> =
        Response.success(FakeDataSource.newStatement)

    override suspend fun addStatementReaction(
        statementId: Int,
        statementReactionDetails: StatementReactionDetails
    ): Response<Unit> =
        Response.success(null)
}