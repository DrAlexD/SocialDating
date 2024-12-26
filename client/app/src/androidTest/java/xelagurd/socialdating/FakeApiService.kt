package xelagurd.socialdating

import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.DefiningTheme
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.network.ApiService

class FakeApiService : ApiService {
    override suspend fun getCategories(): List<Category> = FakeDataSource.categories

    override suspend fun getDefiningThemes(categoryId: Int): List<DefiningTheme> =
        FakeDataSource.definingThemes

    override suspend fun getStatements(definingThemeIds: List<Int>): List<Statement> =
        FakeDataSource.statements
}