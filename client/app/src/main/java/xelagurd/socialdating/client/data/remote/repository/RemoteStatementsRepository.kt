package xelagurd.socialdating.client.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.client.data.model.DataUtils.PAGE_SIZE
import xelagurd.socialdating.client.data.model.details.StatementDetails
import xelagurd.socialdating.client.data.model.details.StatementReactionDetails
import xelagurd.socialdating.client.data.remote.ApiService

@Singleton
class RemoteStatementsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getStatements(
        currentUserId: Int,
        definingThemeIds: List<Int>,
        cursor: String? = null,
        size: Int = PAGE_SIZE
    ) =
        apiService.getStatements(currentUserId, definingThemeIds, cursor, size)

    suspend fun processStatementReaction(statementReactionDetails: StatementReactionDetails) =
        apiService.processStatementReaction(statementReactionDetails)

    suspend fun addStatement(statementDetails: StatementDetails) =
        apiService.addStatement(statementDetails)
}
