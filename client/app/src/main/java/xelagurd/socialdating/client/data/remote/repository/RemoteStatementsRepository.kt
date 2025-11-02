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
    suspend fun getStatements(userId: Int, definingThemeIds: List<Int>) =
        apiService.getStatements(userId, definingThemeIds)

    suspend fun processStatementReaction(statementReactionDetails: StatementReactionDetails) =
        apiService.processStatementReaction(statementReactionDetails)

    suspend fun addStatement(statementDetails: StatementDetails) =
        apiService.addStatement(statementDetails)
}