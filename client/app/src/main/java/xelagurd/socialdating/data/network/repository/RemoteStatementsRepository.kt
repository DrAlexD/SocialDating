package xelagurd.socialdating.data.network.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.network.ApiService

@Singleton
class RemoteStatementsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getStatements(categoryId: Int): List<Statement> =
        apiService.getStatements(categoryId)
}