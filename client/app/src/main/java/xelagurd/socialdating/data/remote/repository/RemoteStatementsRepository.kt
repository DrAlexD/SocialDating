package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.additional.StatementReaction
import xelagurd.socialdating.data.model.details.StatementDetails
import xelagurd.socialdating.data.remote.ApiService

@Singleton
class RemoteStatementsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getStatements(definingThemeIds: List<Int>) =
        apiService.getStatements(definingThemeIds)

    suspend fun postStatementReaction(statementReaction: StatementReaction) =
        apiService.postStatementReaction(statementReaction)

    suspend fun addStatement(statementDetails: StatementDetails) =
        apiService.addStatement(statementDetails)
}