package xelagurd.socialdating.data.remote.repository

import javax.inject.Inject
import javax.inject.Singleton
import xelagurd.socialdating.data.model.Statement
import xelagurd.socialdating.data.model.StatementReactionType
import xelagurd.socialdating.data.remote.ApiService

@Singleton
class RemoteStatementsRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getStatements(definingThemeIds: List<Int>): List<Statement> =
        apiService.getStatements(definingThemeIds)

    suspend fun postStatementReaction(
        userId: Int,
        statementId: Int,
        reactionType: StatementReactionType
    ) = apiService.postStatementReaction(userId, statementId, reactionType)
}