package xelagurd.socialdating

import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.network.ApiService

class FakeApiService : ApiService {
    override suspend fun getCategories(): List<Category> = FakeDataSource.categories

    override suspend fun getStatements(categoryId: Int): List<Statement> = FakeDataSource.statements
}