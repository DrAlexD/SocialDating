package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.remote.ApiService

@Singleton
class RemoteStatementsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getStatements(definingThemeIds: List<Int>): List<Statement> =
        apiService.getStatements(definingThemeIds)
}