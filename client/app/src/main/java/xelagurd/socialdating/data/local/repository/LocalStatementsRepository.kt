package xelagurd.socialdating.data.local.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import xelagurd.socialdating.data.local.dao.StatementsDao
import xelagurd.socialdating.data.model.Statement

@Singleton
class LocalStatementsRepository @Inject constructor(
    private val statementsDao: StatementsDao
) {
    fun getStatements(definingThemeIds: List<Int>): Flow<List<Statement>> =
        statementsDao.getStatements(definingThemeIds)

    suspend fun insertStatements(statements: List<Statement>) =
        statementsDao.insertStatements(statements)
}