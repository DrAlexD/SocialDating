package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.model.additional.StatementReactionDetails
import xelagurd.socialdating.client.data.model.details.StatementDetails
import xelagurd.socialdating.client.data.remote.ApiService

@Singleton
class RemoteStatementsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getStatements(definingThemeIds: List<Int>) =
        apiService.getStatements(definingThemeIds)

    suspend fun addStatementReaction(statementId: Int, statementReactionDetails: StatementReactionDetails) =
        apiService.addStatementReaction(statementId, statementReactionDetails)

    suspend fun addStatement(statementDetails: StatementDetails) =
        apiService.addStatement(statementDetails)
}