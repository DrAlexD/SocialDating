package xelagurd.socialdating

import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.User
import xelagurd.socialdating.data.model.UserCategory
import xelagurd.socialdating.data.model.UserDefiningTheme
import xelagurd.socialdating.data.model.additional.StatementReactionDetails
import xelagurd.socialdating.data.model.details.LoginDetails
import xelagurd.socialdating.data.model.details.RegistrationDetails
import xelagurd.socialdating.data.model.details.StatementDetails
import xelagurd.socialdating.data.remote.ApiService

class FakeApiService : ApiService {
    override suspend fun loginUser(loginDetails: LoginDetails): User? =
        FakeDataSource.users[0]

    override suspend fun registerUser(registrationDetails: RegistrationDetails): User? =
        FakeDataSource.users[0]

    override suspend fun getUser(userId: Int): User? =
        FakeDataSource.users[0]

    override suspend fun getCategories(): List<Category> = FakeDataSource.categories

    override suspend fun getUserCategories(userId: Int): List<UserCategory> =
        FakeDataSource.userCategories

    override suspend fun getDefiningThemes(categoryIds: List<Int>): List<DefiningTheme> =
        FakeDataSource.definingThemes

    override suspend fun getUserDefiningThemes(userCategoryIds: List<Int>): List<UserDefiningTheme> =
        FakeDataSource.userDefiningThemes

    override suspend fun getStatements(definingThemeIds: List<Int>): List<Statement> =
        FakeDataSource.statements

    override suspend fun addStatement(statementDetails: StatementDetails): Statement? =
        FakeDataSource.newStatement

    override suspend fun addStatementReaction(statementId: Int, statementReactionDetails: StatementReactionDetails) {}
}